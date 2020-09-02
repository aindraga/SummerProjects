//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="Runner.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace CallRestAPI
{
    /// <summary>
    /// Class that calls the methods to execute the program
    /// </summary>
    public class Runner
    {
        /// <summary>
        /// Main method that calls the methods to run the program
        /// </summary>
        /// <param name="args"></param>
        public static void Main(string[] args)
        {
            if (StockAPIHelper.InitializeClient() && InputValidation.Validation(args))
            {
                TickerProcessor.TickerListResults(args[0]);
            }
        }
    }
}
