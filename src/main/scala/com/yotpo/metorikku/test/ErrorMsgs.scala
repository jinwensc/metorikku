package com.yotpo.metorikku.test

object ErrorType extends Enumeration {
  val InvalidKeysNonExisting, InvalidKeysNonDefined, DuplicatedResults, DuplicatedResultsHeader,
      MismatchedKeyResultsExpected, MismatchedKeyResultsActual, MismatchedResultsAllCols, InvalidSchemaResults = Value
}


object ResultsType extends Enumeration {
  val expected = Value("Expected")
  val actual = Value("Actual")
  val noRes = Value("")
}
case class ErrorData(errorType: ErrorType.Value, tableName: String, undefinedCols: List[String], allColsKeys: List[String]
                     , outputKey: String, resultType: ResultsType.Value, duplicatedRes: List[Int]
                     , expCount: Int, keyToOutput: String
                     , actCount: Int, expectedRowIndex: Int, sortedRowIndex: Int,
                     mismatchingCols: List[String], mismatchingVals: List[String],
                     invalidSchemaMap: Map[String, List[Int]])

object ErrorData {

  def apply(errorType: ErrorType.Value, outputKey: String, expectedRowIndex: Int, sortedRowIndex: Int,
            mismatchingCols: List[String], mismatchingVals: List[String]): ErrorData = {
    new ErrorData(errorType: ErrorType.Value, "", List[String](), List[String](),
       outputKey, ResultsType.noRes, List[Int](), 0, "", 0,
      expectedRowIndex: Int, sortedRowIndex: Int, mismatchingCols: List[String], mismatchingVals: List[String], Map[String, List[Int]]())
  }
  def apply(errorType: ErrorType.Value): ErrorData = {
    new ErrorData(errorType: ErrorType.Value, "", List[String](), List[String](),
       "", ResultsType.noRes, List[Int](), 0, "", 0, 0, 0, List[String](), List[String](), Map[String, List[Int]]())
  }

  def apply(errorType: ErrorType.Value, tableName: String, undefinedCols: List[String], allColsKeys: List[String]): ErrorData = {
    new ErrorData(errorType, tableName, undefinedCols, allColsKeys,
      "", ResultsType.noRes, List[Int](), 0, "", 0, 0, 0, List[String](), List[String](), Map[String, List[Int]]())
  }

  def apply(errorType: ErrorType.Value, tableName: String): ErrorData = {
    new ErrorData(errorType, tableName, List[String](), List[String](),
      "", ResultsType.noRes, List[Int](), 0, "", 0, 0, 0, List[String](), List[String](), Map[String, List[Int]]())
  }

  def apply(errorType: ErrorType.Value,
            outputKey: String, resType: ResultsType.Value, duplicatedRes: List[Int]): ErrorData = {
    new ErrorData(errorType, "", List[String](), List[String](), outputKey,
      resType, duplicatedRes, 0, "", 0, 0, 0,
      List[String](), List[String](), Map[String, List[Int]]())
  }

  def apply(errorType: ErrorType.Value, expCount: Int, keyToOutput: String, actCount: Int): ErrorData = {
    new ErrorData(errorType: ErrorType.Value, "", List[String](), List[String]()
      , "", ResultsType.noRes, List[Int]()
      , expCount, keyToOutput
      , actCount, 0, 0, List[String](), List[String](), Map[String, List[Int]]())
  }

  def apply(errorType: ErrorType.Value,
            invalidSchemaMap: Map[String, List[Int]]): ErrorData = {
    new ErrorData(errorType, "", List[String](), List[String](),
      "", ResultsType.noRes, List[Int](), 0, "", 0, 0, 0,
      List[String](), List[String](), invalidSchemaMap)
  }
}


object ErrorMsgs {

  def getErrorByType(errorData: ErrorData ): String =
    errorData.errorType match {
      case ErrorType.DuplicatedResultsHeader => {
        "Error: Found duplications in the results: "
      }


      case ErrorType.InvalidKeysNonExisting => {
        s"Defined non existing columns as keys for table ${errorData.tableName}: " +
          s"The bad defined keys: ${errorData.undefinedCols.sortWith(_ < _).mkString(", ")}. " +
          s"All columns defined for ${errorData.tableName} table: ${errorData.allColsKeys.sortWith(_ < _).mkString(", ")}"
      }

      case ErrorType.InvalidKeysNonDefined => {
        s"Unable to read columns defined as keys for table ${errorData.tableName} :<"
      }

      case ErrorType.DuplicatedResults => {
        s"Key = [${errorData.outputKey}] in ${errorData.resultType} rows: ${errorData.duplicatedRes.map(_ + 1).sortWith(_ < _).mkString(", ")}"
      }

      case ErrorType.MismatchedKeyResultsExpected => {
        s"Error: Expected to find 1 " +
          s"time a row with a key [${errorData.keyToOutput}] - found it" +
          s" 0 times"
      }

      case ErrorType.MismatchedKeyResultsActual => {
        s"Error: Didn't expect to find " +
          s"a row with a key [${errorData.keyToOutput}]  - expected for it" +
          s" 0 times"
      }

      case ErrorType.MismatchedResultsAllCols => {
        s"Error: Failed on row ${errorData.expectedRowIndex} with key " +
          s"[${errorData.outputKey}]. \n " +
          s"Column values mismatch on [${errorData.mismatchingCols.sortWith(_ < _).mkString(", ")}] fields " +
          s"with the values [${errorData.mismatchingVals.sortWith(_ < _).mkString(", ")}]"
      }

      case ErrorType.InvalidSchemaResults => {
        val invalidResStr = errorData.invalidSchemaMap.map{case (k, v) => "Table Name = " + k + ", " +
          s"inconsistent result indexes: ${v.sortWith(_ < _).mkString(", ")}"}.mkString("|")

        "Error: Failed while validating the schema of the expected results.  \n" +
          "You must define the same structure (fields) for all expected results. \n" +
          s"The following tables had invalid schema: \n ${invalidResStr}"
      }
    }
}
