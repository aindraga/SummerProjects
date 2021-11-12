import shutil
import os
import send2trash as s2t

class File_Movement():
    def move_file(self, source_file: str, dest_path: str):
        if type(source_file) != str:
            print("Please ensure the source parameter is a string")
            return False

        if os.path.exists(source_file) == False:
            print("The source file does not exist (argument one)")
            return False

        if os.path.isfile(source_file) == False:
            print("Please ensure the path is a file and not a directory")
            return False
        
        if type(dest_path) != str:
            print("Please ensure the destination parameter is a string")
            return False

        if os.path.exists(dest_path) == False:
            print("The destination path does not exist (argument two)")
            return False

        if os.path.isdir(dest_path) == False:
            print("The destination path must be a folder")
            return False
        
        shutil.move(source_file, dest_path)
        return True

    def move_dir(self, source_dir: str, dest_dir: str):
        if type(source_dir) != str:
            print("Please ensure the source dir parameter is a string")
            return False

        if os.path.exists(source_dir) == False:
            print("Please ensure the source path exist")
            return False

        if os.path.isdir(source_dir) == False:
            print("Please ensure the source dir is a directory and not a file")
            return False
                
        if type(dest_dir) != str:
            print("Please ensure the destination dir parameter is a string")
            return False

        if os.path.exists(dest_dir) == False:
            print("Please ensure the destination path exist")
            return False
            
        if os.path.isdir(dest_dir) == False:
            print("Please ensure the dest dir is a directory and not a file")
            return False

        shutil.move(source_dir, dest_dir)
        return True

    def safe_delete_file(self, path: str):
        if type(path) != str:
            print("Please ensure the path parameter is a string")
            return False

        if os.path.exists(path) == False:
            print("Please ensure the path exists")
            return False

        if os.path.isfile(path) == False:
            print("Please ensure the path is a file")
            return False

        s2t.send2trash(path)
        return True

    def safe_delete_folder(self, path: str):
        if type(path) != str:
            print("Please ensure the path parameter is a string")
            return False

        if os.path.exists(path) == False:
            print("Please ensure the path exists")
            return False

        if os.path.isdir(path) == False:
            print("Please ensure the path is a directory")
            return False

        s2t.send2trash(path)
        return True

    def permanent_delete_file(self, path: str):
        if type(path) != str:
            print("Please ensure the path parameter is a string")
            return False

        if os.path.exists(path) == False:
            print("Please ensure the path exists")
            return False

        if os.path.isfile(path) == False:
            print("Please ensure the path is a file")
            return False
        
        os.unlink(path)
        return True

    def permanent_delete_folder(self, path:str):
        if type(path) != str:
            print("Please ensure the path parameter is a string")
            return False

        if os.path.exists(path) == False:
            print("Please ensure the path exists")
            return False

        if os.path.isdir(path) == False:
            print("Please ensure the path is a directory")
            return False
        
        shutil.rmtree(path)
        return True