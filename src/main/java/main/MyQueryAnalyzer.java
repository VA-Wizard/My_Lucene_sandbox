package main;


import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.shingle.FixedShingleFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.io.StringReader;

public class MyQueryAnalyzer extends Analyzer{
    private int maxTokenLength = 255;

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();//my tokenizer
        src.setMaxTokenLength(maxTokenLength);
        TokenStream tok = new LowerCaseFilter(src);
        tok = new StopFilter(tok, CharArraySet.EMPTY_SET);
        tok = new ShingleFilter(tok, 2);
        return new TokenStreamComponents(r -> {
            src.setMaxTokenLength(maxTokenLength);
            src.setReader(r);
        }, tok);
    }

    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new MyQueryAnalyzer(); // or any other analyzer
        TokenStream ts = analyzer.tokenStream("myfield", new StringReader("some text goes here"));
        // The Analyzer class will construct the Tokenizer, TokenFilter(s), and CharFilter(s),
        //   and pass the resulting Reader to the Tokenizer.
        OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAtt = ts.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute positionIncrementAtt = ts.addAttribute(PositionIncrementAttribute.class);

        try {
            ts.reset(); // Resets this stream to the beginning. (Required)
            while (ts.incrementToken()) {
                // Use org.apache.lucene.util.AttributeSource.reflectAsString(boolean)
                // for token stream debugging.
                //System.out.println("token: " + ts.reflectAsString(true));
                System.out.println("token: " + charTermAtt);
                System.out.println("pos: " + positionIncrementAtt.getPositionIncrement());
                //System.out.println("token start offset: " + offsetAtt.startOffset());
                //System.out.println("  token end offset: " + offsetAtt.endOffset());
            }
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } finally {
            ts.close(); // Release resources associated with this stream.
        }
    }
}
