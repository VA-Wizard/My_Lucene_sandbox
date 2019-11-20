package main;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.search.MaxNonCompetitiveBoostAttribute;
import org.apache.lucene.search.spell.DirectSpellChecker;
import org.apache.lucene.search.spell.StringDistance;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRefBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.PriorityQueue;

public class MyDirectSpellChecker extends DirectSpellChecker {

    private StringDistance distance = INTERNAL_LEVENSHTEIN;
    private int minPrefix = 1;
    /**
     * Provide spelling corrections based on several parameters.
     *
     * @param term The term to suggest spelling corrections for
     * @param numSug The maximum number of spelling corrections
     * @param ir The index reader to fetch the candidate spelling corrections from
     * @param docfreq The minimum document frequency a potential suggestion need to have in order to be included
     * @param editDistance The maximum edit distance candidates are allowed to have
     * @param accuracy The minimum accuracy a suggested spelling correction needs to have in order to be included
     * @param spare a chars scratch
     * @return a collection of spelling corrections sorted by <code>ScoreTerm</code>'s natural order.
     * @throws IOException If I/O related errors occur
     */
    protected Collection<ScoreTerm> suggestSimilar(Term term, int numSug, IndexReader ir, int docfreq, int editDistance,
                                                   float accuracy, final CharsRefBuilder spare) throws IOException {

        AttributeSource atts = new AttributeSource();
        MaxNonCompetitiveBoostAttribute maxBoostAtt =
                atts.addAttribute(MaxNonCompetitiveBoostAttribute.class);
        Terms terms = MultiTerms.getTerms(ir, term.field());
        if (terms == null) {
            return Collections.emptyList();
        }
        FatFingerTermsEnum e = new FatFingerTermsEnum(terms, atts, term, editDistance, Math.max(minPrefix, editDistance-1), true);
        final PriorityQueue<ScoreTerm> stQueue = new PriorityQueue<>();

        BytesRef queryTerm = new BytesRef(term.text());
        BytesRef candidateTerm;
        ScoreTerm st = new ScoreTerm();
        BoostAttribute boostAtt = e.attributes().addAttribute(BoostAttribute.class);
        while ((candidateTerm = e.next()) != null) {
            // For FuzzyQuery, boost is the score:
            float score = boostAtt.getBoost();
            // ignore uncompetitive hits
            if (stQueue.size() >= numSug && score <= stQueue.peek().boost) {
                continue;
            }

            // ignore exact match of the same term
            if (queryTerm.bytesEquals(candidateTerm)) {
                continue;
            }

            int df = e.docFreq();

            // check docFreq if required
            if (df <= docfreq) {
                continue;
            }

            final String termAsString;
            if (distance == INTERNAL_LEVENSHTEIN) {
                // delay creating strings until the end
                termAsString = null;
            } else {
                spare.copyUTF8Bytes(candidateTerm);
                termAsString = spare.toString();
                score = distance.getDistance(term.text(), termAsString);
            }

            if (score < accuracy) {
                continue;
            }

            // add new entry in PQ
            st.term = BytesRef.deepCopyOf(candidateTerm);
            st.boost = score;
            st.docfreq = df;
            st.termAsString = termAsString;
            st.score = score;
            stQueue.offer(st);
            // possibly drop entries from queue
            st = (stQueue.size() > numSug) ? stQueue.poll() : new ScoreTerm();
            maxBoostAtt.setMaxNonCompetitiveBoost((stQueue.size() >= numSug) ? stQueue.peek().boost : Float.NEGATIVE_INFINITY);
        }

        return stQueue;
    }
}
