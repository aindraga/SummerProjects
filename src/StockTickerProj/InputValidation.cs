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
    /// Class for validating user input
    /// </summary>
    public class InputValidation
    {
        /// <summary>
        /// Method for validating user input
        /// </summary>
        /// <param name="validateArgs"> string array of inputs that are being validated </param>
        /// <returns> a boolean that indicates if the validation passed </returns>
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

                if (extension == ".json")
                {
                    return true;
                }

                return false;
            }

            return true;
        }
    }
}
