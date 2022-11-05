# Requirements

## Minimum requirement

Write an app in the tech stack mentioned which exposes an API that returns the
Create RESTful APIs, use Spring Boot (MVC Architecture) for bean initialization and Service injection configuration.

## Frontend

- **(ok)** Make a simple form that takes in the file name and gives the option to upload a PDF file.
- **(ok)** Test for the following validations:
  -- File Type: .pdf
  -- File Size: 1MB
- **(ok)** Show a list of past uploaded files with links.

## Backend

- **(ok)** Create an API Utility that intakes the given file and uploads it to any cloud storage. Example S3 in AWS.
- **(ok half done, add db impl)** Rename the PDF file to the given name a and add a 7-digit Hash.
- **(ok half done, add db impl)** The Hash must never repeat
- **(ok)** Mime Type: content/pdf
- Make sure that the browser does not download the PDF file but only shows it, unlike the link it in the guide.

- Test for the following validations:
    - File Type: .pdf
    - File Size: 1MB
- **(ok)** Map the name of the file with Name+Hash and store it in the database.
- **(ok)** Use Hibernate (ORM) or Spring data.
- **(ok)** Use JAVA 8 (or higher) features like Stream, Lambda Expressions, Concurrency API, etc.
- **(ok)** Create a 2nd API that sends a JSON containing a list of past uploaded file links

### Example app

https://s3-ap-southeast-1.amazonaws.com/he-public-data/dummyc4d1859.pdf