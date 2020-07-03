using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;

namespace FileCopyNetF
{
    class Program
    {
        static void Main(string[] args)
        {
            if (InputValidation(args))
            {
                CopyAndMove(args);
            }
        }

        private static bool InputValidation(string[] inputValArgs)
        {
            string source = inputValArgs[0];
            string dest = inputValArgs[1];
            bool sourceFolderExists = Directory.Exists(source);
            bool destFolderExists = Directory.Exists(dest);

            if (!sourceFolderExists)
            {
                Console.WriteLine("Error: There is an issue with your source input. Please try again.");
                return false;
            }


            if (!destFolderExists)
            {
                Console.WriteLine("Error: The destination directory is not valid. Please try again");
                return false;
            }
            return true;
        }


        private static void CopyAndMove(string[] copyAndMoveArgs)
        {
            string[] allFiles = Directory.GetFiles(copyAndMoveArgs[0]);
            foreach (string filePath in allFiles)
            {
                FileInfo file = new FileInfo(filePath);
                
                DateTime creationTime = file.CreationTime;
                string year = creationTime.Year.ToString();
                string month = creationTime.Month.ToString();
                string day = creationTime.Day.ToString();

                string timeExtension = "\\" + year + "\\" + month + "\\" + day;
                string newDir = copyAndMoveArgs[1] + timeExtension;

                if (!Directory.Exists(newDir))
                {
                    Directory.CreateDirectory(newDir);
                }

                File.Copy(filePath, newDir + ); // second arg is invalid
            }
        }
    }
}
