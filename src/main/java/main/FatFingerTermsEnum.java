package main;

import org.apache.lucene.index.*;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.search.FuzzyTermsEnum;
import org.apache.lucene.search.MaxNonCompetitiveBoostAttribute;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.util.*;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.util.automaton.LevenshteinAutomata;

import java.io.IOException;
import java.util.Arrays;

public class FatFingerTermsEnum extends BaseTermsEnum {

    @Override
    public SeekStatus seekCeil(BytesRef text) throws IOException {
        return null;
    }

    @Override
    public void seekExact(long ord) throws IOException {

    }

    @Override
    public BytesRef term() throws IOException {
        return null;
    }

    @Override
    public long ord() throws IOException {
        return 0;
    }

    @Override
    public int docFreq() throws IOException {
        return 0;
    }

    @Override
    public long totalTermFreq() throws IOException {
        return 0;
    }

    @Override
    public PostingsEnum postings(PostingsEnum reuse, int flags) throws IOException {
        return null;
    }

    @Override
    public ImpactsEnum impacts(int flags) throws IOException {
        return null;
    }

    @Override
    public BytesRef next() throws IOException {
        return null;
    }
}
