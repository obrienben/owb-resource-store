package nz.govt.natlib.ndha;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@RestController
@RequestMapping("/")
public class StoreController {

	ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
//	Service service = context.getBean(Service.class);
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

	@RequestMapping(value = "add/{filename:.+}", method = RequestMethod.GET)
	public void putWarc(HttpServletResponse response, ModelMap model, @PathVariable("filename") String filename) {

		try {
			// Add warc to data store
			if(!addWarc(filename)){
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
				e1.printStackTrace();
			}
			return;
		}
	}


	@RequestMapping(value = "{filename:.+}", method = RequestMethod.GET)
	public void streamWarc(HttpServletRequest request, HttpServletResponse response, ModelMap model, @PathVariable("filename") String filename) {

		try {
			// Lookup warc file path
			Path warcPath = getWarc(filename);
			if(warcPath == null){
				response.sendError(404, "Warc filename requested was not found");
				return;
			}

			// Parse range requested in Header
			Range range = Range.parseHeader(request, Files.size(warcPath));
			if(range == null){
				response.sendError(400, "Invalid or no range requested");
				return;
			}

			// Construct response
			response.setStatus(206);
			response.setHeader("Content-Range", range.toString());
			response.setHeader("Content-Length", Long.toString(range.length));
			response.setContentType("application/warc");

			// Write requested range from warc file to response
			WritableByteChannel out = Channels.newChannel(response.getOutputStream());
			FileChannel fileChannelIn = FileChannel.open(warcPath, StandardOpenOption.READ);
			fileChannelIn.transferTo(range.start, range.length, out);

		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(500, "Unable to retrieve warc record");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
	}


	private boolean addWarc(String filename) {
		return source.addWarc(filename);
	}

	private Path getWarc(String filename) {
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