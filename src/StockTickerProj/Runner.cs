//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="Runner.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace CallRestAPI
{
    using System;
    /// <summary>
    /// Class that calls and runs the methods
    /// </summary>
    public class Runner
    {
        /// <summary>
        /// Main method that runs all the methods
        /// </summary>
        /// <param name="args">the .txt file pathway passed</param>
        public static void Main(string[] args)
        {
            if (StockAPIHelper.InitializeClient() && InputValidation.Validation(args))
            {
                TickerProcessor.TickerListResults(args[0]);
            }
        }
    }
}