# sf-attchment-archive 
This project demonstrates how to add Salesforce Attachments archive/download functionality to Salesforce. This utility is designed to deploy on Heroku.
 
The project also demonstrates how to use Heroku to develop and deploy common services to enhance Salesforce solutions. 

Basic functionality provides a web application hosting Java zip archiving service. This service can be used via simple URL from any visual force page or possibly from standard actions. 
Invoking the services with list of Attachment IDs will produce the ZIP file and respond to request with download stream, creating user experience of downloading set of Attachments in a single zip file.

The service also provides SOAP web service interface that will archive Attachments in same manner but instead will save resulting zip file in AWS S3 storage and return download URL to user.

