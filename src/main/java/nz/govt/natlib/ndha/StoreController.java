package nz.govt.natlib.ndha;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.spy.memcached.MemcachedClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 */
@RestController
@RequestMapping("/")
public class StoreController {

	private static final Logger log = LogManager.getLogger(StoreController.class);
	ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
	private static Map<String, String> warcs = new HashMap<String, String>();
	private StoreSource source = (StoreSource) ctx.getBean("storeSource");

	public StoreController(){
		warcs.put("WEB-20160603014432482-00000-9193-ubuntu-8443.warc", "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\WEB-20160603014432482-00000-9193-ubuntu-8443.warc");
	}


	@RequestMapping(method = RequestMethod.GET)
	public void noFilenamePresent(HttpServletResponse response, ModelMap model) {
		try {
			response.sendError(400, "Please supply warc filename");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	@RequestMapping(method = RequestMethod.POST)
	public void updateStore(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		log.info("Received new filepaths - updating Resource Store");
		try {
//			HashMap<String, String> filePaths = null;
			HashMap<String, List<String>> results = new HashMap<>();
			BufferedReader reader = request.getReader();
			StringBuffer content = new StringBuffer();
			String line;
			while((line = reader.readLine()) != null){
				content.append(line);
			}


			Type hashMapType = new TypeToken<HashMap<String, String>>(){}.getType();
			HashMap<String, String> filePaths = new Gson().fromJson(content.toString(), hashMapType);

			List<String> success = new ArrayList<>();
			List<String> fail = new ArrayList<>();

			// Create new store connection
				establishStoreConnection(true);

			// Add warcs to data store
			for(String key : filePaths.keySet()){
				log.debug("Adding key to store: " + key);
				if(addWarc(key, filePaths.get(key))){
					success.add(key);
					log.debug("Filepath successfully added to Resource Store for key: " + key);
				}
				else{
					fail.add(key);
				}
			}
				terminateStoreConnection();
			results.put("success", success);
			results.put("fail", fail);

			response.setStatus(200);
			if(!fail.isEmpty()) {
				response.setStatus(400);
				log.warn("One or more filepaths were unable to be stored.");
			}


			// Send results back
			byte[] resultsJSON = new Gson().toJson(results).getBytes();
			response.setContentType("application/json");
			response.setHeader("Content-Length", Long.toString(resultsJSON.length));
			response.getOutputStream().write(resultsJSON);
			return;

		} catch (Exception e) {
			log.error("Failed to update Resource Store", e);
			terminateStoreConnection();
			try {
				response.sendError(500, "Unable to retrieve warc record");
			} catch (IOException e1) {
				log.error("Failed to send 500 response", e1);
			}
			return;
		}
	}


	@RequestMapping(value = "add/{filename:.+}", method = RequestMethod.GET)
	public void putWarc(HttpServletResponse response, ModelMap model, @PathVariable("filename") String filename) {

		try {
			// Add warc to data store
			if(!addWarc(filename, "")){
				response.sendError(500, "Unable to update Store with warc file.");
				return;
			}

			// Construct response
			response.setStatus(201);

		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(500, "Unable to retrieve warc record");
			} catch (IOException e1) {
				log.error("Failed to send 500 response", e1);
			}
			return;
		}
	}


	@RequestMapping(value = "{filename:.+}", method = RequestMethod.GET)
	public void streamWarc(HttpServletRequest request, HttpServletResponse response, ModelMap model, @PathVariable("filename") String filename) {

		try {
			// Create new store connection
			establishStoreConnection(false);

			// Lookup warc file path
			Path warcPath = getWarc(filename);
			if(warcPath == null){
				response.sendError(404, "Warc filename requested was not found");
				log.warn("Requested filename was not found in Resource Store: " + filename);
				terminateStoreConnection();
				return;
			}

			// Parse range requested in Header
			Range range = Range.parseHeader(request, Files.size(warcPath));
			if(range == null){
				response.sendError(400, "Invalid or no range requested");
				log.warn("Invalid or no range requested supplied in request.");
				return;
			}

			terminateStoreConnection();

			// Construct response
			response.setStatus(206);
			response.setHeader("Content-Range", range.toString());
			response.setHeader("Content-Length", Long.toString(range.length));
			response.setContentType("application/warc");

			// Write requested range from warc file to response
			WritableByteChannel out = Channels.newChannel(response.getOutputStream());
			FileChannel fileChannelIn = FileChannel.open(warcPath, StandardOpenOption.READ);
			fileChannelIn.transferTo(range.start, range.length, out);

			log.debug("Requested file was successfully streamed: " + filename);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed to read from Resource Store", e);
			terminateStoreConnection();
			try {
				response.sendError(500, "Unable to retrieve warc record");
			} catch (IOException e1) {
				log.error("Failed to send 500 response", e1);
			}
			return;
		}
	}


	private void establishStoreConnection(boolean flag){
		log.debug("Establishing new connection to Store");
		try {
			source.startConnection(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void terminateStoreConnection() {
		log.debug("Terminating connection to Store");
		try {
			source.endConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean addWarc(String filename, String filePath) throws Exception {
		return source.addWarc(filename, filePath);
	}

	private Path getWarc(String filename) throws Exception {
		String warcPath = source.getWarc(filename);
		if(warcPath != null){
			return Paths.get(warcPath);
		}
		return null;
	}



	static class Range {
		static final Pattern BYTES_SPEC_PATTERN = Pattern.compile("([0-9]+)?-([0-9]+)?");

		final long start, length, total;

		public Range(long start, long length, long total) {
			this.start = start;
			this.length = length;
			this.total = total;
		}

		public String toString() {
			return String.format("%d-%d/%d", start, start + length - 1, total);
		}

		static Range parseHeader(HttpServletRequest request, long fileSize) {
			String headerValue = request.getHeader("Range");
			if (headerValue == null || !headerValue.startsWith("bytes=") || headerValue.equals("bytes=")) {
				return null;
			}
			String rangeValue = headerValue.substring("bytes=".length());
			Range range = parseByteRange(rangeValue, fileSize);
			if (range == null) {
				return null;
			}
			return range;
		}

		static Range parseByteRange(String spec, long fileSize)  {
			Matcher m = BYTES_SPEC_PATTERN.matcher(spec);
			if (m.matches()) {
				String startText = m.group(1);
				String endText = m.group(2);
				if (startText != null) {
					long start = Long.parseLong(startText);
					long end = endText == null ? fileSize : Long.parseLong(endText);
					return new Range(start, end - start + 1, fileSize);
				} else if (endText != null) {
					long tail = Long.parseLong(endText);
					if (tail >= fileSize) {
						return null;
					}
					return new Range(fileSize - tail, tail, fileSize);
				}
			}
			throw new NumberFormatException("Bad byte range: " + spec);
		}
	}
}