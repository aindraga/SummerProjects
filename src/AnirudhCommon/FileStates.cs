//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="FileStates.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace AnirudhCommon
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Newtonsoft.Json;

    /// <summary>
    /// Class that maintains state of files that were copied. Class contains both serialization and deserialization methods
    /// </summary>
    public class FileStates
    {
        /// <summary>
        /// Initializes a new instance of the FileStates class
        /// </summary>
        public FileStates()
        {
            this.CopiedFiles = new HashSet<string>(StringComparer.OrdinalIgnoreCase);
        }

        /// <summary>
        /// Gets the files that were copied in run time process
        /// </summary>
        public HashSet<string> CopiedFiles { get; set; }

        /// <summary>
        /// Serializes the list of copied files 
        /// </summary>
        /// <param name="jsonPath"> the file path where the file states are stored </param>
        /// <returns> a boolean of whether it the process was successful </returns>
        public bool Serialize(string jsonPath)
        {
            string serializedObj = JsonConvert.SerializeObject(this.CopiedFiles);
            File.WriteAllText(jsonPath, serializedObj);
            return true;
        }

        /// <summary>
        /// Deserializes the list to be referenced in Copying.cs during the copy process
        /// </summary>
        /// <param name="jsonPath"> the file path where the file states are stored </param>
        /// <returns> a boolean of whether the process was successful </returns>
        public bool DeSerialize(string jsonPath)
        {
            if (!File.Exists(jsonPath))
            {
                return false;
            }

            string allContent = File.ReadAllText(jsonPath);
            this.CopiedFiles = JsonConvert.DeserializeObject<HashSet<string>>(allContent);
            return true;
        }
    }
}