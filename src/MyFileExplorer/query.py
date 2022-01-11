import db

def query(search_str=None, item_type=None, file_ext=None, deeper_search=False) -> bool:
    if search_str is None:
        print("Please ensure 'search_str' parameter is not 'None'.")
        return False
    if search_str is not None and type(search_str) != str:
        print("Please ensure 'search_str' parameter is of type string.")
        return False
    if item_type is not None and type(item_type) != str:
        print("Please ensure 'item_type' is of type 'tuple'.")
        return False
    if file_ext is not None and type(file_ext) != tuple:
        print("Please ensure 'file_ext' is of type tuple.")
        return False
    if item_type is not None and (item_type != "files" and item_type != "folders"):
        print("Please ensure the 'item_type' parameter is either 'files' or 'folders'.")
        return False
    if item_type is None and file_ext is not None:
        print("Ensure the 'item_type' parameter is 'files'.")
        return False
    if item_type == "folders" and file_ext is not None:
        print("Invalid query with 'folders' and 'file_ext'. Please try again.")
        return False

    db.reader_lock.acquire()

    if item_type == "files" and file_ext is not None and deeper_search == False:
        count: int = 0
        for file_extensions in file_ext:
            if file_extensions not in db.files_dictionary["files"].keys():
                print("The", file_extensions, "does not exist.")
                db.reader_lock.release()
                return False
            else:
                if search_str in db.files_dictionary["files"][file_extensions].keys():
                    count += 1
                    print(db.files_dictionary["files"][file_extensions][search_str])

        db.reader_lock.release()
        if count > 0:
            return True
        else:
            print("No results were found.")
            return False

    elif item_type == "files" and file_ext is not None and deeper_search == True:
        count: int = 0
        for file_extensions in file_ext:
            if file_extensions not in db.files_dictionary["files"].keys():
                print("The", file_extensions, "does not exist.")
                db.reader_lock.release()
                return False
            else:
                for file_names in db.files_dictionary["files"][file_extensions].keys():
                    if search_str in file_names:
                        count += 1
                        print(db.files_dictionary["files"][file_extensions][file_names])

        db.reader_lock.release()
        if count > 0:
            return True
        else:
            print("No results were found.")
            return False

    elif (item_type == "files") and (file_ext is None or file_ext == ()) and deeper_search == False:
        count: int = 0
        for file_extensions in db.files_dictionary["files"].keys():
            if search_str in db.files_dictionary["files"][file_extensions].keys():
                count += 1
                print(db.files_dictionary["files"][file_extensions][search_str])

        db.reader_lock.release()
        if count > 0:
            return True
        else:
            print("No results found.")
            return False

    elif item_type == "files" and (file_ext is None or file_ext == ()) and deeper_search == True:
        count: int = 0
        for file_extensions in db.files_dictionary["files"].keys():
            for file_names in db.files_dictionary["files"][file_extensions].keys():
                if search_str in file_names:
                    count += 1
                    print(db.files_dictionary["files"][file_extensions][file_names])
                
        db.reader_lock.release()
        if count > 0:
            return True
        else:
            print("No results found.")
            return False

    elif item_type == "folders" and deeper_search == False:
        if search_str in db.files_dictionary["folders"].keys():
            print(db.files_dictionary["folders"][search_str])
            db.reader_lock.release()
            return True
        else:
            print("No results found.")
            db.reader_lock.release()
            return False

    elif item_type == "folders" and deeper_search == True:
        count: int = 0
        for folder_names in db.files_dictionary["folders"].keys():
            if search_str in folder_names:
                print(db.files_dictionary["folders"][folder_names])
                count += 1

        db.reader_lock.release()
        if count > 0:
            return True
        else:
            print("No results found.")
            return False