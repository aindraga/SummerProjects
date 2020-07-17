//-----------------------------------------------------------------------------------
// <copyright company="Anirudh Indraganti" file="Runner.cs">
//      Copyright ©️ Anirudh Indraganti. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------------------
namespace FileCopyNetF
{
    using AnirudhCommon;

    public class Runner
    {
        public static void Main(string[] args)
        {
            if (Validation.InputValidation(args))
            {
                Copying.CopyAndMove(args[0], args[1]);
            }
        }
    }
}