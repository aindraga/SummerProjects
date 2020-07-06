//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="Program.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace FileCopyNetF
{
    using System;
    using System.IO;

    /// <summary>
    /// Class containing entry point into the program
    /// </summary>
    public class Program
    {
        /// <summary>
        /// Entry point into the program
        /// </summary>
        /// <param name="args">Command line parameters</param>
        public static void Main(string[] args)
        {
            if (InputValidation(args))
            {
                CopyAndMove(args[0], args[1]);
            }
        }    

        /// <summary>
        /// Entry into the input validation
        /// </summary>
        /// <param name="inputValArgs"> Command line arguments </param>
        /// <returns> a boolean of whether the validation passed </returns>
        private static bool InputValidation(string[] inputValArgs)
        {
            string source = inputValArgs[0];
            string dest = inputValArgs[1];
            bool sourceFolderExists = Directory.Exists(source);
            bool destFolderExists = Directory.Exists(dest);

            if (inputValArgs.Length < 2)
            {
                Console.WriteLine("Entered too few arguments. Please try again.");
                return false;
            }

            if (inputValArgs.Length > 2)
            {
                Console.WriteLine("Too many arguments were passed. Please try again.");
                return false;
            }

            if (!sourceFolderExists)
            {
                Console.WriteLine("Error: There is an issue with your source input. Please try again.");
                return false;
            }

            if (!destFolderExists)
            {
                Console.WriteLine("Error: The destination directory is not valid. Please try again");
                return false;
            }

            return true;
        }

        /// <summary>
        /// Entry into the point where the files in the source folder will be copied and moved
        /// </summary>
        /// <param name="source"> the source folder path </param>
        /// <param name="destination"> the destination folder path </param>
        /// <returns> a boolean that determines if an exception was thrown in the process of method execution </returns>
        private static bool CopyAndMove(string source, string destination)
        {
            try
            {
                string[] allFiles = Directory.GetFiles(source);

                foreach (string filePath in allFiles)
                {
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

                    File.Copy(filePath, newFilePath);
                }

                return true;
            }
            catch (Exception toMakeFalse)
            {
                Console.WriteLine(toMakeFalse.ToString());
                return false;
            }
        }
    }
} 
