//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="Runner.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace FileCopyNetF
{
    using AnirudhCommon;

    /// <summary>
    /// Runs the code for copying files
    /// </summary>
    public class Runner
    {
        /// <summary>
        /// Main method that calls the methods needed to run
        /// </summary>
        /// <param name="args"> Command line arguments </param>
        public static void Main(string[] args)
        {
            if (Validation.InputValidation(args))
            {
                Copying.CopyAndMove(args[0], args[1]);
            }
        }
    }
}