import shutil
import pathlib
import xml.etree.ElementTree as xet
import pandas as pd
import os

class My_File_Class():
    def local_delete(self, delete_folder: str) -> bool:
        if type(delete_folder) != str:
            print("Ensure the folder parameter is a string")
            return False

        if os.path.exists(delete_folder) == False:
            print("The inputted folder path does not exist")
            return False

        if len(os.listdir(delete_folder)) == 0:
            print("The folder is already empty.")
            return True

        for file in os.listdir(delete_folder):
            os.remove(os.path.join(delete_folder, file))

        if (len(os.listdir(delete_folder)) == 0):
            print("All files were successfully deleted")
            return True
        else:
            print("Not all files were deleted from the given directory.")
            return False

    def local_dir_count(self, dir: str) -> int:
        if type(dir) != str:
            print("Ensure the directory parameter is a string")
            return -1

        if os.path.exists(dir):
            return len(os.listdir(dir))

        print("The directory does not exist")
        return -1

    def local_xml_move(self, source: str, dest: str) -> bool:
        if type(source) != str:
            print("Please ensure the source parameter is a string")
            return False
        
        if os.path.exists(source) == False:
            print("The source folder path does not exist. Please try again")
            return False

        if os.path.isdir(source) == False:
            print("The input source path must a directory")
            return False
        
        if type(dest) != str:
            print("Please ensure the dest parameter is a string")
            return False
        
        if os.path.exists(dest) == False:
            print("The dest directory does not exist. Please try again")
            return False

        if os.path.isdir(dest) == False:
            print("Ensure the input destination path is a directory")
            return False

        for source_file in os.listdir(source):
            source_ext = pathlib.Path(source_file).suffix
            
            if source_ext == ".xml":
                shutil.move(os.path.join(source, source_file), os.path.join(dest, source_file))
        
        return True

    def xml_to_csv(self, source: str, dest: str) -> bool:
        if type(source) != str:
            print("Please ensure the source parameter is of type string")
            return False

        if type(dest) != str:
            print("Please ensure the dest parameter is of type string")
            return False

        if os.path.exists(source) == False:
            print("Source folder does not exist")
            return False

        if os.path.exists(dest) == False:
            print("Destination folder does not exist")
            return False
            
        file_lst = [file for file in os.listdir(source)]
        for file in file_lst:
            rows = []
            columns = []
            rows_dict = dict()
            root = xet.parse(os.path.join(source, file)).getroot()
            for element in root.iter():
                if element.text.isspace() == False:
                    columns.append(element.tag)
                    rows_dict[element.tag] = element.text
            rows.append(rows_dict)
            data_frame = pd.DataFrame(rows, columns=columns)
            file_new_ext = os.path.join(dest, file[:-4] + ".csv")
            data_frame.to_csv(file_new_ext)

        return True

    def change_file_to_jpg(self, dir:str) -> bool:
        if type(dir) != str:
            print("Please ensure the dir parameter is a string")
            return False
            
        if os.path.exists(dir) == False:
            print("The inputted directory does not exist")
            return False

        all_files_stems = set()
        for file in os.listdir(dir):
            all_files_stems.add(pathlib.Path(file).stem)

        for file in os.listdir(dir):
            if pathlib.Path(file).stem not in all_files_stems and pathlib.Path(file).suffix != ".jpg":
                pathlib.Path(file).rename(os.path.join(dir, file)).with_suffix(".jpg")

        return True