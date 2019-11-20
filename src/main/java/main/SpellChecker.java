package main;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spell.DirectSpellChecker;
import org.apache.lucene.search.spell.WordBreakSpellChecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SpellChecker {

    //Directory index = new RAMDirectory();
    static SpellChecker spellChecker = new SpellChecker();
    static IndexReader indexReader;

    public static void main(String[] args) throws Exception{
        Indexer indexer = new Indexer();
        indexer.indexWords(Files.lines(Paths.get("I:\\Java projects\\git\\Lucene_sandbox\\src\\main\\resources\\word_dict.txt")));
        indexReader =  DirectoryReader.open(indexer.getIndex());


        SpellCheckerStandard.m("pung", 50, indexReader);

        spellChecker.checkCommonErrors();

    }

    public boolean compareWithBestResul(String line, String[] expected) throws Exception {
        String suggest = SpellCheckerStandard.m(line, 10, indexReader);
        for (String s : expected) {
            if(s.equals(suggest)){
                return true;
            }
        }
        return false;
    }

    public void checkCommonErrors() throws IOException {
        AtomicInteger all = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();
        Files.lines(Paths.get("I:\\Java projects\\git\\Lucene_sandbox\\src\\main\\resources\\common_words_error.txt")).forEach(line->{
            all.incrementAndGet();
            String[] split = line.split("->");
            try {
                if(!compareWithBestResul(split[0], split[1].split(", "))){
                    errors.incrementAndGet();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println("all words:" + all.get());
        System.out.println("errors:" + errors.get());
        System.out.println("percent:" + ((1 - ((double)errors.get() / all.get()))) * 100);
    }
}
