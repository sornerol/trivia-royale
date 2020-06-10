package com.triviaroyale.importer

import com.opencsv.bean.CsvToBeanBuilder
import com.triviaroyale.importer.data.QuestionImport
import groovy.transform.CompileStatic

@CompileStatic
class QuestionsFromCsv {

    public static final String CSV_PREFIX = '.csv'
    public static final String OUTPUT_DIRECTORY_BASE = 'output'

    static void main(String[] args) {
        if (args.length < 1) {
            println('USAGE: groovy QuestionsFromCsv.groovy [filename]')
            System.exit(1)
        }

        String fileName = args[0]
        String category = fileName - CSV_PREFIX
        List<QuestionImport> importedQuestions = new CsvToBeanBuilder<QuestionImport>(new FileReader(args[0]))
                .withType(QuestionImport)
                .build()
                .parse()

        String outputDirectoryPath = "$OUTPUT_DIRECTORY_BASE/${category}_${System.currentTimeMillis()}"
        File outputDirectory = new File(outputDirectoryPath)
        outputDirectory.mkdirs()
        importedQuestions.each { importedQuestion ->
            String newFilename = "$outputDirectoryPath/${UUID.randomUUID()}.json"
            File outputFile = new File(newFilename)
            outputFile.write(importedQuestion.toQuestion().toJson())
        }
    }

}
