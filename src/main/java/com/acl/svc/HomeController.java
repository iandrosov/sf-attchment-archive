package com.acl.svc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.acl.io.CloudFileUtil;
import com.sforce.soap.enterprise.wsc.QueryResult;
import com.sforce.soap.enterprise.sobject.wsc.Attachment;
import com.sforce.ws.ConnectorConfig;
import com.sforce.soap.enterprise.wsc.Connector;
import com.sforce.soap.enterprise.wsc.EnterpriseConnection;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static final String SVC_TEMP_DIR = "svcstore";
	private static final String SVC_TEMP_FILE = "default_store";
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome to Attachment Services, the client locale is "+ locale.toString());
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value="/zipattachment")
	public ModelAndView handleArchiveRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
		System.out.println("Call Attachment archive");
		String idStrList = request.getParameter("id");
		String sessionId = request.getParameter("sessionid");
		String serverUrl = request.getParameter("serverurl");
		String zip_file_name = request.getParameter("zip");
		
		if (idStrList != null && sessionId != null && serverUrl != null 
			&& serverUrl.length() > 0 && sessionId.length() > 0 && idStrList.length() > 0) {
		
			try{
				String[] idList = idStrList.split(",");
				
				ConnectorConfig config = new ConnectorConfig();
				config.setSessionId(sessionId);
				config.setServiceEndpoint(serverUrl);
				EnterpriseConnection connection = Connector.newConnection(config);
				
				if (connection != null){
					
					String dir = this.createTempDir(); //"svcstore_yyyymmddhhmm/";
					for (int i = 0; i < idList.length; i++){
						 getAttachments(dir, idList[i], connection);
					}

					File f = new File(dir);
					if (f.exists() && f.isDirectory() && f.listFiles() != null && f.listFiles().length > 0){
						if (zip_file_name == null)
							zip_file_name = this.createTempFileName(); // "default_store.zip";

						response.setContentType("application/zip");
						response.setHeader("Content-Disposition","inline; "+zip_file_name+"; charset=utf-8");
						// Write the output to a file to return stream from download
						ServletOutputStream ouputStream = response.getOutputStream();

						// Archive and return a stream
						CloudFileUtil cfu = new CloudFileUtil();
						cfu.zipDir(dir, zip_file_name);

						byte[] buffer = new byte[8192];
						int read = 0;
						FileInputStream in = new FileInputStream(zip_file_name);
						while (-1 != (read = in.read(buffer))) {
							ouputStream.write(buffer, 0, read);
						}
						in.close();
						
						// Clean up local temp store
						cfu.deleteFile(zip_file_name); // Delete temp zip file
						cfu.removeDirectory(dir); // Delete temp attachment directory
					}
				}else{
					System.out.println("Login BAD ");
				}
						
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Query Attachment files for a given ID. Due to SFDC API restriction taht allows only
	 * single Attachment body to be returned by query this requires separate SOQL query for each
	 * given file ID
	 * 
	 * @param dir - String temporary local stor where to store attachments
	 * @param s - String Id for Attachment to be archived
	 * @param connection
	 */
	private void getAttachments(String dir, String s, EnterpriseConnection connection) {
		 try{
			   // Handle Attachments one ata time Body of attachments can be only from single query multi select NOT supported
			   QueryResult queryResults = connection.query("Select Id, ParentId, Name, ContentType, Body From Attachment WHERE id = '"+s+"'");
			
			   if (queryResults.getSize() > 0) {
				   for (int i=0;i<queryResults.getRecords().length;i++) {
			          // cast the SObject to a strongly-typed Contact
			        	Attachment a = (Attachment)queryResults.getRecords()[i];
			        	writeAttachmentFile(dir, a.getId()+"-"+a.getName(),a.getBody());
			        	System.out.println("Id: " + a.getId() + " - Name: "+a.getName()+" "+" - Account: "+a.getParentId());
			        }
			      }
			   
		  }catch (Exception e) {
		      e.printStackTrace();
		  }    
	}
	/**
	 * Write attachment file to tremporary storage to be archived into zip file
	 *   
	 * @param dir
	 * @param fileName
	 * @param bdy
	 */
	private void writeAttachmentFile(String dir, String fileName, byte[] bdy){
		try{
			  CloudFileUtil cfu = new CloudFileUtil();
			  cfu.createDirs(dir);	
			  String filePath = dir+fileName;
			  FileOutputStream fos = new FileOutputStream(filePath);//File OutPutStream is used to write Binary Contents like pictures
			  fos.write(bdy);
			  fos.close();
		  }catch (IOException e){
			  System.out.println(e.getMessage());
		  }
	}

	/**
	 * Generate time stamp based directory name
	 * for use as temporary storage locally on server side
	 * 
	 * @return
	 */
	private String createTempDir(){
		Date dt = new Date();
		String dir = SVC_TEMP_DIR + "_" + Long.toString(dt.getTime()) + "/";
		return dir;
	}
	
	/**
	 * Generate timestamp based temp file as default in case the file name was not
	 * provided
	 * 
	 * @return String file name in form default_svcstor_yyyymmddhhmmss.zip
	 */
	private String createTempFileName(){
		Date dt = new Date();
		String file_name = SVC_TEMP_FILE + "_" + Long.toString(dt.getTime()) + ".zip";
		
		return file_name;
	}

}
