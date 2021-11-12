import os
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from __future__ import print_function

class Google():
    # If modifying these scopes, delete the file token.json.
    SCOPES = ['https://www.googleapis.com/auth/drive']

    def google_auth(self):
        """
        Shows basic usage of the Drive v3 API.
        Prints the names and ids of the first 10 files the user has access to.
        """
        creds = None
        # The file token.json stores the user's access and refresh tokens, and is
        # created automatically when the authorization flow completes for the first
        # time.
        if os.path.exists('token.json'):
            creds = Credentials.from_authorized_user_file('token.json', self.SCOPES)
        
        # If there are no (valid) credentials available, let the user log in.
        if not creds or not creds.valid:
            if creds and creds.expired and creds.refresh_token:
                creds.refresh(Request())
            else:
                flow = InstalledAppFlow.from_client_secrets_file(
                    'CLIENT_SECRETS_FILE.com.json', self.SCOPES)
                
                creds = flow.run_local_server()
            
            # Save the credentials for the next run
            with open('token.json', 'w') as token:
                token.write(creds.to_json())

        service = build('drive', 'v3', credentials=creds)
        return service

    def google_folder_check(self, google_service):
        """
        This function calls the Google Drive v3 API.
        """ 
        page_token = None

        # Executes a query to find the 'images' folder
        while True:
            response = google_service.files().list(q="name = 'images' and mimeType = 'application/vnd.google-apps.folder'",
                spaces="drive",
                fields="nextPageToken, files(id, name)",
                pageToken=page_token).execute()

            # Checks if folder was found. If not it creates it
            file_list = response.get('files', [])
            if (len(file_list) > 0):
                print("Found 'images' folder")
                folder_id = file_list[0]["id"]
                return folder_id
            else:
                name = "images"
                folder_metadata = {'name': name, 'mimeType': 'application/vnd.google-apps.folder'}
                folder = google_service.files().create(body=folder_metadata, fields='id').execute()
                print("Created 'images' folder") 
                return folder.get("id")