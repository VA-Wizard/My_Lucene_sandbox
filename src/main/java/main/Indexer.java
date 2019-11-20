package main;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class Indexer {
    private Directory index = new RAMDirectory();

    public Indexer(){

    }

    public void indexWords(Stream<String> words) {
        DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
        try {
            IndexWriter w = new IndexWriter(index, new IndexWriterConfig());
            words.forEach(word->{
                String origin = word.split(" ")[0];
                String occur = word.split(" ")[1];
                Document doc = new Document();
                doc.add(new StringField("origin", origin, Field.Store.YES));
                doc.add(new StringField("phonetic", doubleMetaphone.doubleMetaphone(origin), Field.Store.YES));
                doc.add(new NumericDocValuesField("score", Long.parseLong(occur)));
                try {
                    w.addDocument(doc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Directory getIndex() {
        return index;
    }
}
