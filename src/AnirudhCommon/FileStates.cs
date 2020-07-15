using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AnirudhCommon
{
    public class FileStates
    {
        public string copiedFiles { get; set; }

        public static bool Serialize(FileStates filestate, string jsonPath)
        {
            string serializedObj = JsonConvert.SerializeObject(filestate);
            File.WriteAllText(jsonPath, serializedObj);
            return true;
        }
        public static bool DeSerialize(string jsonPath)
        {
            string allContent = File.ReadAllText(jsonPath);
            List<string> filesCopied = JsonConvert.DeserializeObject<List<string>>(allContent);
            return true;
        }
    }
}