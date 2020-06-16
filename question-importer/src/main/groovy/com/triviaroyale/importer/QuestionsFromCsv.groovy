package com.triviaroyale.importer

import com.opencsv.bean.CsvToBeanBuilder
import com.triviaroyale.importer.data.QuestionImport
import groovy.transform.CompileStatic

import java.text.SimpleDateFormat

@CompileStatic
class QuestionsFromCsv {

    public static final String OUTPUT_DIRECTORY_BASE = 'output'

    static void main(String[] args) {
        if (args.length < 1) {
            println('USAGE: groovy QuestionsFromCsv.groovy [filename]')
            System.exit(1)
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat('yyyy-MM-dd_HH:mm:ss')
        String exportTime = simpleDateFormat.format(new Date())
        String fileName = args[0]
        List<QuestionImport> importedQuestions = new CsvToBeanBuilder<QuestionImport>(new FileReader(fileName))
                .withType(QuestionImport)
                .build()
                .parse()

        importedQuestions.each { importedQuestion ->
            String outputDirectoryPath = "$OUTPUT_DIRECTORY_BASE/${importedQuestion.category}_${exportTime}"
            File outputDirectory = new File(outputDirectoryPath)
            outputDirectory.mkdirs()
            String newFilename = "$outputDirectoryPath/${UUID.randomUUID()}.json"
            File outputFile = new File(newFilename)
            outputFile.write(importedQuestion.toQuestion().toJson())
        }
    }

}
