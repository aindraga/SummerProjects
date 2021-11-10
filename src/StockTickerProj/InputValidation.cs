//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="InputValidation.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------

namespace CallRestAPI
{
    using System;
    using System.IO;

    /// <summary>
    /// Input Validation class that contains two methods
    /// </summary>
    public class InputValidation
    {
        /// <summary>
        /// Parses args and checks file path existence
        /// </summary>
        /// <param name="validateArgs">string array that contains the .txt file path argument</param>
        /// <returns>a boolean indicating if the validation was successful</returns>
        public static bool Validation(string[] validateArgs)
        {
            if (validateArgs.Length > 1)
            {
                Console.WriteLine("Too many arguments. Please try again.");
                return false;
            }

            if (validateArgs.Length < 1)
            {
                Console.WriteLine("Too few arguments. Please try again.");
                return false;
            }

            if (File.Exists(validateArgs[0]))
            {
                FileInfo fileInfo = new FileInfo(validateArgs[0]);
                string extension = fileInfo.Extension;

                if (extension == ".txt")
                {
                    return true;
                }

                return false;
            }

            return false;
        }

        /// <summary>
        /// Gets the .txt file and reads all lines to a string array
        /// </summary>
        /// <param name="filepath">.txt file path</param>
        /// <returns>Array of all parsed tickers</returns>
        public static string[] FileValidation(string filepath)
        {
            string[] allTickers = File.ReadAllLines(filepath);

            for (int i = 0; i < allTickers.Length; i++)
            {
                allTickers[i] = allTickers[i].ToUpper();
            }

            return allTickers;
        }
    }
}