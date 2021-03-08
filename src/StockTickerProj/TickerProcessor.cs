//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="TickerProcessor.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace CallRestAPI
{
    using System;
    using System.Collections.Generic;
    using System.Data.SqlClient;
    using System.IO;
    using System.Net.Http;
    using System.Threading.Tasks;
    using AnirudhCommon;

    /// <summary>
    /// class for calling REST API and processing data
    /// </summary>
    public class TickerProcessor
    {
        /// <summary>
        /// Method that calls the API and retrieves data from a specific ticker
        /// </summary>
        /// <param name="ticker"> ticker that user wants price for </param>
        /// <returns> TickerModel object that contains the ticker's current price </returns>
        public static async Task<TickerModel> TickerInfo(string ticker)
        {
            ticker = ticker.ToUpper();
            string url = $"https://finnhub.io/api/v1/quote?symbol={ ticker }&token=bscdcsnrh5rfbrs0tfdg";

            using (HttpResponseMessage response = await StockAPIHelper.ApiClient.GetAsync(url))
            {
                if (response.IsSuccessStatusCode)
                {
                    TickerModel myModel = await response.Content.ReadAsAsync<TickerModel>();
                    return myModel;
                }
                else
                {
                    Console.WriteLine("The API was uncallable.");
                    return null;
                }
            }
        }

        /// <summary>
        /// Method for processing a file of tickers and getting their current prices
        /// </summary>
        /// <param name="jsonPath"> pathway of the file containing all tickers </param>
        /// <returns> a boolean that indicates if the process succeeded </returns>
        public static bool TickerListResults(string jsonPath)
        {
            if (!File.Exists(jsonPath))
            {
                Console.WriteLine("The file does not exist");
                return false;
            }

            string connectionString = "Server = tcp:tickertest.database.windows.net,1433; " +
                "Initial Catalog = TickerData; " +
                "Persist Security Info = False; " +
                "User ID = pqknyrbqdseamzbhrpeg; " +
                "Password = YAS6N%8t%amK78W^Z5myH@W&@^9XP9; " +
                "MultipleActiveResultSets = False; " +
                "Encrypt = True; " +
                "TrustServerCertificate = False; " +
                "Connection Timeout = 30";

            using (SqlConnection connection = new SqlConnection(connectionString))
            {
                connection.Open();
                FileStates allTickers = new FileStates();
                allTickers.DeSerialize(jsonPath);
                HashSet<string> tickerSet = allTickers.CopiedFiles;
                DateTime dateRunTime = DateTime.Now;

                foreach (string ticker in tickerSet)
                {
                    string validatedTicker = ticker.ToUpper();
                    TickerModel tickerInfo = TickerInfo(validatedTicker).Result;
                    SqlCommand command = new SqlCommand("INSERT INTO TickerSharePrices (Ticker, SharePrice, DateSharePrice) " +
                                                        "Values ('" + validatedTicker + "'," + tickerInfo.c + ", " + "'" +
                                                        dateRunTime.ToString("o") + "'" + ")", connection);
                    command.ExecuteNonQuery();
                }
            }

            return true;
        }
    }
}
