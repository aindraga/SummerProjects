//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="TickerModel.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------

namespace CallRestAPI
{
    using System.Collections.Generic;
    using Newtonsoft.Json.Linq;
    /// <summary>
    /// Class that creates a model for the called data to be stored in.
    /// </summary>
    public class SignalModel
    {
        /// <summary>
        /// Gets or sets a string of the buy/sell signal of multiple indicators 
        /// </summary>
        public JObject technicalAnalysis { get; set; }
    }
}
