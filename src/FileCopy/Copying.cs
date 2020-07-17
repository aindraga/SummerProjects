//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="Copying.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace FileCopyNetF
{
    using System;
    using System.IO;
    using AnirudhCommon;

    /// <summary>
    /// Contains method that copies the files from the source to the destination directory
    /// </summary>
    public class Copying
    {
        /// <summary>
        /// Method to copy and move the files into the destination directory. Contains instructions for when errors occur as well
        /// </summary>
        /// <param name="source"> Source input </param>
        /// <param name="destination"> Destination input </param>
        /// <returns> boolean of whether the method ran all the way through </returns>
        public static bool CopyAndMove(string source, string destination)
        {
            FileStates filestate = new FileStates();
            string workingDir = Environment.CurrentDirectory;
            string json = Path.Combine(workingDir, "JsonMaintainState.json");
            filestate.DeSerialize(json);
            string[] allFiles = Directory.GetFiles(source, "*", SearchOption.AllDirectories);

            for (int i = 0; i < allFiles.Length; i++)
            {
                string filePath = allFiles[i];

                if (filestate.CopiedFiles != null)
                {
                    bool fileCopied = filestate.CopiedFiles.Contains(filePath);

                    if (fileCopied)
                    {
                        continue;
                    }
                }

                FileInfo file = new FileInfo(filePath);

                DateTime creationTime = file.CreationTime;
                string year = creationTime.Year.ToString();
                string month = creationTime.Month.ToString();
                string day = creationTime.Day.ToString();

                if (month.Length == 1)
                {
                    month = "0" + month;
                }

                if (day.Length == 1)
                {
                    day = "0" + day;
                }

                string fileName = Path.GetFileName(filePath);
                string timeExtension = Path.Combine(year, month, day);
                string newDirPath = Path.Combine(destination, timeExtension);
                string newFilePath = Path.Combine(newDirPath, fileName);

                if (!Directory.Exists(newDirPath))
                {
                    Directory.CreateDirectory(newDirPath);
                }

                try
                {
                    File.Copy(filePath, newFilePath);
                    filestate.CopiedFiles.Add(filePath);
                }
                catch (IOException)
                {
                    Console.WriteLine("The file: " + filePath + " was not able to be copied");
                    continue;
                }

                if (!Crypto.HashComparison(filePath, newFilePath))
                {
                    Console.WriteLine("The hashes are not similar. Something went wrong in the copy process");
                    return false;
                }
            }

            filestate.Serialize(json);
            return true;
        }
    }
}
