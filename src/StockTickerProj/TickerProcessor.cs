//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="TickerProcessor.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace CallRestAPI
{
    using System;
    using System.Data.SqlClient;
    using System.IO;
    using System.Net.Http;
    using System.Threading.Tasks;
    
    /// <summary>
    /// Class that contains the data processing methods
    /// </summary>
    public class TickerProcessor
    {
        /// <summary>
        /// Method for calling the REST API and assigning the needed data to TickerModel variables
        /// </summary>
        /// <param name="ticker">tickers contained in the .txt file</param>
        /// <returns>a TickerModel object if the response is a success, otherwise it returns null.</returns>
        public static async Task<string> TickerInfo(string ticker)
        {
            ticker = ticker.ToUpper();
            string url = $"https://finnhub.io/api/v1/scan/technical-indicator?symbol={ ticker }&resolution=D&token=API_TOKEN";

            using (HttpResponseMessage response = await StockAPIHelper.ApiClient.GetAsync(url))
            {
                if (response.IsSuccessStatusCode)
                {
                    SignalModel myModel = await response.Content.ReadAsAsync<SignalModel>();
                    string content = myModel.technicalAnalysis["signal"].ToString();
                    return content;
                }
                else
                {
                    Console.WriteLine("The API was uncallable.");
                    return null;
                }
            }
        }

        public static async Task<QuoteModel> QuoteInfo(string ticker)
        {
            ticker = ticker.ToUpper();
            string url = $"https://finnhub.io/api/v1/quote?symbol={ ticker }&token=bscdcsnrh5rfbrs0tfdg";

            using (HttpResponseMessage response = await StockAPIHelper.ApiClient.GetAsync(url))
            {
                if (response.IsSuccessStatusCode)
                {
                    QuoteModel myModel = await response.Content.ReadAsAsync<QuoteModel>();
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
        /// A method that loops through the tickers and calls the REST API and writes the data to an SQL table.
        /// </summary>
        /// <param name="filepath">.txt file path that contains the tickers</param>
        /// <returns>a boolean indicating whether the above process succeeded or failed.</returns>
        public static bool TickerListResults(string filepath)
        {
            if (!File.Exists(filepath))
            {
                Console.WriteLine("The file does not exist");
                return false;
            }

            string connectionString = "Server=tcp:annuserver.database.windows.net,1433;" +
                "Initial Catalog=ProjectsDB;" +
                "Persist Security Info=False;" +
                "User ID=aindraga;" +
                "Password=PASSWORD;" +
                "MultipleActiveResultSets=False;" +
                "Encrypt=True;" +
                "TrustServerCertificate=False;" +
                "Connection Timeout=30;";

            using (SqlConnection connection = new SqlConnection(connectionString))
            {
                try 
                { 
                    connection.Open(); 
                }
                catch (Exception message)
                {
                    Console.WriteLine(message.Message);
                    return false;
                }

                string[] allTickers = InputValidation.FileValidation(filepath);
                DateTimeOffset dateNow = DateTimeOffset.Now;
                string dateRunTime = dateNow.Month.ToString() + "/" + dateNow.Day.ToString() + "/" + dateNow.Year.ToString();

                for (int i = 0; i < allTickers.Length; i++)
                {
                    string tickerInfo = TickerInfo(allTickers[i]).Result;
                    QuoteModel quoteInfo = QuoteInfo(allTickers[i]).Result;

                    string query = $"INSERT INTO TickerDataTable (tickers, share_price, buy_signal, run_date)" +
                        $" VALUES ('{allTickers[i]}', '{quoteInfo.c}', '{tickerInfo}', '{dateRunTime}')";

                    SqlCommand command = new SqlCommand(query, connection);
                    command.ExecuteNonQuery();
                }
                connection.Close();
            }
            return true;
        }
    }
}