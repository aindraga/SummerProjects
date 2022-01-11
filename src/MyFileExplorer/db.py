import os
import pathlib
from typing import List
import tkinter as tk
from tkinter import filedialog
from collections import deque
from watchdog.events import FileSystemEventHandler
import threading
from watchdog.observers import Observer
import time
import json

files_dictionary: dict = {
    "files": {

        }, 
    "folders": {

    }
}

update_dict: dict = {
    "files": {
        "created": deque(), 
        "deleted": deque(), 
        "moved": {

        }
    }, 
    "folders": {
        "created": deque(), 
        "deleted": deque(), 
        "moved": {

        }
    }
}

re_entry_lock = threading.Lock()

def flatten_and_sort_directory() -> bool:
    root: tk.Tk = tk.Tk()
    root.withdraw()

    user_input_folder: str = filedialog.askdirectory()

    for subfolder_root, folders_in_subfolders, files in os.walk(user_input_folder, topdown=False):
        for file_name in files:
            full_file_path: str = os.path.join(subfolder_root, file_name).replace("/", "\\")
            add_pathway(full_file_path)

        for folder_name_in_subfolder in folders_in_subfolders:
            full_folder_path: str = os.path.join(subfolder_root, folder_name_in_subfolder).replace(
                "/", "\\"
            )
            add_pathway(full_folder_path)

    return True

def write_to_json():
    with open("files.json", "w") as files_json:
        json.dump(files_dictionary, files_json, indent=3)

    return True
        

def search_lst(current_pathway: str, current_lst: List[str]) -> int:
    left_index, right_index = 0, len(current_lst) - 1
    while left_index <= right_index:
        left_path: str = current_lst[left_index]
        right_path: str = current_lst[right_index]
        if (left_index == right_index) and (left_path == current_pathway):
            return left_index
        else:
            if left_path == current_pathway:
                return left_index
            if right_path == current_pathway:
                return right_index

        left_index += 1
        right_index -= 1

    return -1

def add_pathway(current_path: str) -> bool:
    is_directory: bool = os.path.isdir(current_path)
    path_basename: str = os.path.basename(current_path)

    re_entry_lock.acquire()

    if is_directory == False:
        file_ext: str = pathlib.Path(path_basename).suffix
        file_stem: str = pathlib.Path(path_basename).stem

        file_ext_dict: dict = files_dictionary["files"]
        if file_ext not in file_ext_dict.keys():
            files_dictionary["files"][file_ext] = {}

        files_in_file_ext: dict = file_ext_dict[file_ext]
        if file_stem not in files_in_file_ext.keys():
            files_dictionary["files"][file_ext][file_stem] = [current_path]
        else:
            files_dictionary["files"][file_ext][file_stem].append(current_path)

    else:
        folders_dict: dict = files_dictionary["folders"]
        if path_basename not in folders_dict.keys():
            files_dictionary["folders"][path_basename] = [current_path]

        else:
            files_dictionary["folders"][path_basename].append(current_path)

    re_entry_lock.release()
    return True

def remove_pathway(current_path: str) -> bool:
    is_dir: bool = os.path.isdir(current_path)
    path_basename: str = os.path.basename(current_path)

    re_entry_lock.acquire()

    if is_dir == True and (path_basename in files_dictionary["folders"].keys()):
        searched_lst: List[str] = files_dictionary["folders"][path_basename]
        path_index: int = search_lst(current_path, searched_lst)
        
        if path_index != -1:
            searched_lst.pop(path_index)

        if len(files_dictionary["folders"][path_basename]) == 0:
            files_dictionary["folders"].pop(path_basename)
    
    else:
        file_ext: str = pathlib.Path(path_basename).suffix
        file_stem: str = pathlib.Path(path_basename).stem

        if (file_ext in files_dictionary["files"]) and (file_stem in files_dictionary["files"][file_ext]):
            searched_lst: List[str] = files_dictionary["files"][file_ext][file_stem]
            path_index: int = search_lst(current_path, searched_lst)
            
            if path_index != -1:
                files_dictionary["files"][file_ext][file_stem].pop(path_index)

            if len(files_dictionary["files"][file_ext][file_stem]) == 0:
                files_dictionary["files"][file_ext].pop(file_stem)

    re_entry_lock.release()
    
    return True

def pop_from_create():
    print("Pop Created Called")
    while True:
        time.sleep(1.5)
        if len(update_dict["files"]["created"]) == 0 and len(update_dict["folders"]["created"]) == 0:
            print("Nothing in Created")
            continue
        if len(update_dict["files"]["created"]) > 0:
            while len(update_dict["files"]["created"]) > 0:
                add_pathway(update_dict["files"]["created"][0])
                update_dict["files"]["created"].popleft()

            print("Created Files List Processed")
            continue

        if len(update_dict["folders"]["created"]) > 0:
            while len(update_dict["folders"]["created"]) > 0:
                add_pathway(update_dict["folders"]["created"][0])
                update_dict["folders"]["created"].popleft()

            print("Created Folders List Processed")
            continue

def pop_from_delete():
    print("Pop Deleted Called")
    while True:
        time.sleep(3)
        if len(update_dict["files"]["deleted"]) == 0 and len(update_dict["folders"]["deleted"]) == 0:
            print("Nothing in Deleted")
            continue
        
        if len(update_dict["files"]["deleted"]) > 0:
            while len(update_dict["files"]["deleted"]) > 0:
                remove_pathway(update_dict["files"]["deleted"][0])
                update_dict["files"]["deleted"].popleft()

            print("Deleted Files List Processed")
            continue

        if len(update_dict["folders"]["deleted"]) > 0:
            while len(update_dict["folders"]["deleted"]) > 0:
                remove_pathway(update_dict["folders"]["deleted"][0])
                update_dict["folders"]["deleted"].popleft()
            
            print("Deleted Folders List Processed")
            continue

def pop_from_moved():
    print("Pop Moved Called")
    while True:
        time.sleep(7)
        if len(update_dict["files"]["moved"]) == 0 and len(update_dict["folders"]["moved"]) == 0:
            print("Nothing in Moved")
            continue
        if len(update_dict["files"]["moved"]) > 0:
            for original_path in update_dict["files"]["moved"].keys():
                remove_pathway(original_path)
                add_pathway(update_dict["files"]["moved"][original_path])

            update_dict["files"]["moved"] = dict()
            print("Moved Files Dict Processed")
            continue

        if len(update_dict["folders"]["moved"]) > 0:
            for original_path in update_dict["folders"]["moved"].keys():
                remove_pathway(original_path)
                add_pathway(update_dict["folders"]["moved"][original_path])

            update_dict["folders"]["moved"] = dict()
            print("Moved Folders Dict Processed")
            continue

        

class MonitorFolder(FileSystemEventHandler):
    def on_created(self, event):
        if event.is_directory == False:
            update_dict["files"]["created"].append(event.src_path)
        else:
            update_dict["folders"]["created"].append(event.src_path)

        print("Created Function Called: " + event.src_path)

    def on_deleted(self, event):
        if event.is_directory == False:
            update_dict["files"]["deleted"].append(event.src_path)
        else:
            update_dict["folders"]["deleted"].append(event.src_path)

        print("Deleted Function Call: " + event.src_path)

    def on_moved(self, event):
        if event.is_directory == False:
            update_dict["files"]["moved"][event.src_path] = event.dest_path
        else:
            update_dict["folders"]["moved"]["original"] = event.src_path
            update_dict["folders"]["moved"]["new"] = event.dest_path

        print("Moved Function Called:", "Source:",  event.src_path, "Dest:", event.dest_path)

if __name__ == "__main__":
    flatten_and_sort_directory()
    write_to_json()
    observer: Observer = Observer()
    observer.schedule(MonitorFolder(), path="C:\\", recursive=True)
    observer.start()

    try:
        created_pop_thread: threading.Thread = threading.Thread(name="Created Thread", target=pop_from_create)
        deleted_pop_thread: threading.Thread = threading.Thread(name="Deleted Thread", target=pop_from_delete)
        moved_pop_thread: threading.Thread = threading.Thread(name="Moved Thread", target=pop_from_moved)
        
        created_pop_thread.start()
        deleted_pop_thread.start()
        moved_pop_thread.start()

    except KeyboardInterrupt:
        observer.stop()
        observer.join()
        created_pop_thread.join()
        deleted_pop_thread.join()
        moved_pop_thread.join()