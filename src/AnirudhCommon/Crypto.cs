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
        public static string SHA256HashForFile(string filePath)
        {
            bool fileExists = File.Exists(filePath);
            if (fileExists == true)
            {
                FileInfo fileInfo = new FileInfo(filePath);
                using (SHA256 fileSHA256 = SHA256.Create())
                {
                    FileStream fileStream = fileInfo.Open(FileMode.Open);
                    fileStream.Position = 0;
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

            if (filePath1Exists & filePath2Exists)
            {
                string sha256File1 = SHA256HashForFile(filePath1);
                string sha256File2 = SHA256HashForFile(filePath2);

                if (sha256File1 == sha256File2)
                {
                    return true;
                }

                Console.WriteLine("Not the same file");
                return false;
             }

            Console.WriteLine("One of the file paths is invalid");
            return false;
            }
        }
 }