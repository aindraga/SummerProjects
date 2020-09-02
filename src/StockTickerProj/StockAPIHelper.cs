//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="StockAPIHelper.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace CallRestAPI
{
    using System.Net.Http;
    using System.Net.Http.Headers;

    /// <summary>
    /// Class that instantiates HttpClient that calls web service
    /// </summary>
    public static class StockAPIHelper
    {
        /// <summary>
        /// Gets or sets an HttpClient object for the whole application
        /// </summary>
        public static HttpClient ApiClient { get; set; }

        /// <summary>
        /// Initializes the HttpClient
        /// </summary>
        /// <returns> a boolean indicating if the process was successful </returns>
        public static bool InitializeClient()
        {
            if (ApiClient == null)
            {
                ApiClient = new HttpClient();
            }

            ApiClient.DefaultRequestHeaders.Accept.Clear();
            ApiClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

            return true;
        }
    }
}