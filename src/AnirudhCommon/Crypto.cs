using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace AnirudhCommon
{
    /// <summary>
    /// Crypto class that contains methods for multiple uses of SHA256
    /// </summary>
    public class Crypto
    {
        /// <summary>
        /// Gets the hash of a single file
        /// </summary>
        /// <param name="filePath"> pathway of the file user wants to pass in </param>
        /// <returns> string that contains the hash value if everything worked properlly otherwise null </returns>
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
                        bitconversion = bitconversion.Replace("-", "");
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
            bool FilePath1Exists = File.Exists(filePath1);
            bool FilePath2Exists = File.Exists(filePath2);

            if (FilePath1Exists & FilePath2Exists)
            {
                string Sha256File1 = SHA256HashForFile(filePath1);
                string Sha256File2 = SHA256HashForFile(filePath2);

                if (Sha256File1 == Sha256File2)
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