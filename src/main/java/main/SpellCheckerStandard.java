package main;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.*;

import java.io.IOException;
import java.util.Comparator;

public class SpellCheckerStandard {
    public static String m(String phrase, int size, IndexReader ir) throws IOException {


        //return getDirectCorrection(phrase, size, ir);

        return phonetic(phrase, size, ir);

        // word breaking
//        WordBreakSpellChecker wordBreakSpellChecker = new WordBreakSpellChecker();
//        SuggestWord[][] suggestWords = wordBreakSpellChecker.suggestWordBreaks(new Term("origin", "somesphrase"), 5, ir, SuggestMode.SUGGEST_ALWAYS, WordBreakSpellChecker.BreakSuggestionSortMethod.NUM_CHANGES_THEN_MAX_FREQUENCY);
//        new Object();


    }

    private static String phonetic(String phrase, int size, IndexReader ir) throws IOException {
        SuggestWord[] suggestWordsPhonetic;
        Term phonetcTerm = new Term("phonetic", new DoubleMetaphone().doubleMetaphone(phrase));
        int phonetic = ir.docFreq(phonetcTerm);
        if (phonetic > 0){
            IndexSearcher searcher = new IndexSearcher(ir);
            TopDocs docs = searcher.search(new TermQuery(phonetcTerm), size);
            ScoreDoc[] hits = docs.scoreDocs;

            suggestWordsPhonetic = new SuggestWord[hits.length];

            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                SuggestWord suggestWord = new SuggestWord();
                suggestWord.string = d.get("origin");
                suggestWordsPhonetic[i] = suggestWord;
            }
        }else{
            suggestWordsPhonetic = new SuggestWord[0];
        }
        if(suggestWordsPhonetic.length == 0){
            return "";
        }else{
            return suggestWordsPhonetic[0].string;
        }
    }

    private static String getDirectCorrection(String phrase, int size, IndexReader ir) throws IOException {
        DirectSpellChecker directSpellChecker = new DirectSpellChecker();
        SuggestWord[] suggestWordsLev = directSpellChecker.suggestSimilar(new Term("origin", phrase), size, ir, SuggestMode.SUGGEST_ALWAYS);
        int docfreq = ir.docFreq(new Term("origin", phrase));
        if (docfreq > 0){
            return phrase;
        }
        if(suggestWordsLev.length == 0){
            return "";
        }else{
            return suggestWordsLev[0].string;
        }
    }


}
