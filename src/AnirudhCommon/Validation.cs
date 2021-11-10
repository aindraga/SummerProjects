//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="Validation.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace AnirudhCommon
{
    using System;
    using System.IO;

    /// <summary>
    /// Contains method for input validation of code
    /// </summary>
    public class Validation
    {
        /// <summary>
        /// Method for input validation
        /// </summary>
        /// <param name="inputValArgs"> array of strings that contain the source and destination directories </param>
        /// <returns> boolean indicating if the inputs were valid </returns>
        public static bool InputValidation(string[] inputValArgs)
        {
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

            string source = inputValArgs[0];
            string dest = inputValArgs[1];

            if (!Directory.Exists(source))
            {
                Console.WriteLine("Error: There is an issue with your source input. Please try again.");
                return false;
            }

            if (!Directory.Exists(dest))
            {
                Console.WriteLine("Error: The destination directory is not valid. Please try again");
                return false;
            }

            return true;
        }
    }
}
