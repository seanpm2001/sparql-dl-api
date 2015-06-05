package de.derivo.sparqldlapi.impl;

import de.derivo.sparqldlapi.QueryArgument;
import de.derivo.sparqldlapi.types.QueryArgumentType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 10/03/15
 */
public class LiteralTranslator {

    private final static Pattern LITERAL_PATTERN_IRI_QUOTED = Pattern.compile("\"([^@\"]+)(@([^\"]+))?\"(\\^\\^<([^>]+)>)?");
    private final static Pattern LITERAL_PATTERN = Pattern.compile("\"([^@\"]+)(@([^\"]+))?\"(\\^\\^(.+))?");

    private OWLDataFactory dataFactory;

    public LiteralTranslator(OWLDataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }

    public OWLLiteral toOWLLiteral(QueryArgument argument) {
        Matcher matcher = LITERAL_PATTERN.matcher(argument.getValue());
        if(matcher.matches()) {
            String literal = matcher.group(1);
            String lang = matcher.group(3);
            String datatypeIRI = matcher.group(5);
            if (lang != null) {
                return dataFactory.getOWLLiteral(literal, lang);
            } else if (datatypeIRI != null) {
                return dataFactory.getOWLLiteral(literal, dataFactory.getOWLDatatype(IRI.create(datatypeIRI)));
            }
        }
        return dataFactory.getOWLLiteral(argument.getValue(), dataFactory.getRDFPlainLiteral());
    }

    public static QueryArgument toQueryArgument(OWLLiteral literal) {
        return toQueryArgument(literal.getLiteral(), literal.getLang(), literal.getDatatype().getIRI().toString());
    }

    public static QueryArgument toQueryArgument(String literal, String lang, String datatypeIRI) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(literal);
        if(!lang.isEmpty()) {
            sb.append("@");
            sb.append(lang);
        }
        sb.append("\"^^");
        sb.append(datatypeIRI);
        return new QueryArgument(QueryArgumentType.LITERAL, sb.toString());
    }
}