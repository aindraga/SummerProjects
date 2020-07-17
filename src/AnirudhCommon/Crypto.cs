//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="Crypto.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace AnirudhCommon
{
    using System;
    using System.IO;
    using System.Security.Cryptography;

    /// <summary>
    /// Crypto class that contains methods for multiple uses of SHA256
    /// </summary>
    public class Crypto
    {
        /// <summary>
        /// Gets the hash of a single file
        /// </summary>
        /// <param name="filePath"> pathway of the file user wants to pass in </param>
        /// <returns> string that contains the hash value if everything worked properly otherwise null </returns>
        public static string SHA256Clean(string filePath)
        {
            bool fileExists = File.Exists(filePath);

            if (fileExists)
            {
                FileStream fileStream = new FileStream(filePath, FileMode.Open, FileAccess.Read);
                string hash = GetSHA(fileStream);
                return hash;
            }
            else
            {
                Console.WriteLine("The file does not exist");
                return null;
            }
        }

        /// <summary>
        /// Compares hashes of two different files
        /// </summary>
        /// <param name="filePath1"> first file path for comparison </param>
        /// <param name="filePath2"> second file path for comparison </param>
        /// <returns> boolean of whether the files have the same hashes </returns>
        public static bool HashComparison(string filePath1, string filePath2)
        {
            bool filePath1Exists = File.Exists(filePath1);
            bool filePath2Exists = File.Exists(filePath2);

            if (filePath1Exists && filePath2Exists)
            {
                string sha256File1 = SHA256Clean(filePath1);
                string sha256File2 = SHA256Clean(filePath2);

                if (sha256File1 == sha256File2)
                {
                    Console.WriteLine(filePath1 + ": " + sha256File1 + "|| " + filePath2 + ": " + sha256File2);
                    return true;
                }

                Console.WriteLine("Not the same file:");
                Console.WriteLine(filePath1 + ": " + sha256File1 + "|| " + filePath2 + ": " + sha256File2);
                return false;
            }
            else
            {
                if (!filePath1Exists)
                {
                    Console.WriteLine("The first path does not exist");
                    return false;
                }

                if (!filePath2Exists)
                {
                    Console.WriteLine("The second path does not exist");
                    return false;
                }

                return true;
            }
        }

        /// <summary>
        /// Getting SHA if file is locked for writing
        /// </summary>
        /// <param name="filepath"> pathway of file for SHA </param>
        /// <returns> returns SHA if file is locked for writing otherwise null </returns>
        public static string SHA256Dirty(string filepath)
        {
            bool fileExists = File.Exists(filepath);

            if (fileExists)
            {
                FileStream fileStream = new FileStream(filepath, FileMode.Open, FileAccess.Read, FileShare.ReadWrite);
                string hash = GetSHA(fileStream);
                return hash;
            }

            Console.WriteLine("File does not exist");
            return null;
        }

        /// <summary>
        /// Gets SHA given a file stream object
        /// </summary>
        /// <param name="fileStream"> file stream to find SHA for </param>
        /// <returns> SHA value if no exception is thrown otherwise null </returns>
        private static string GetSHA(FileStream fileStream)
        {
            using (SHA256 fileSHA256 = SHA256.Create())
            {
                using (fileStream)
                {
                    try
                    {
                        byte[] byteHash = fileSHA256.ComputeHash(fileStream);
                        string bitconversion = BitConverter.ToString(byteHash);
                        bitconversion = bitconversion.Replace("-", string.Empty);
                        return bitconversion;
                    }
                    catch (Exception toMakeFalse)
                    {
                        Console.WriteLine(toMakeFalse.ToString());
                        return null;
                    }
                    finally
                    {
                        fileStream.Close();
                    }
                }
            }
        }
    }
}