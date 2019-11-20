package main;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.Operations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class New {

    int[] myAlpha = {'q','w','e','r','t','y','u','i','o','p','[',']'};

    public static void main(String[] args) throws IOException {
        Automaton a = new Automaton();
        int start = a.createState();
        int c = a.createState();
        //int end = a.createState();
        a.addTransition(start, c, 'a', 'a');
        a.setAccept(c, true);
        a.finishState();

        System.out.println(Operations.run(a, "a"));
    }
}
