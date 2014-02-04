package ac.uk.susx.tag.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

public class WikiTextConverter {
	
	public WikiTextConverter(){}
	
	public String convertToPlainText(String orig) {
		WikiModel wikiModel = new WikiModel("http://www.mywiki.com/wiki/${image}", "http://www.mywiki.com/wiki/${title}");
        String plainStr = wikiModel.render(new PlainTextConverter(), orig);
        System.err.println(plainStr);
        return cleanString(plainStr);
        //return plainStr;
	}
	
	public String cleanString(String orig){
		String remStart = "\\\\n+\\w";
		Pattern patt = Pattern.compile(remStart);
		Matcher m = patt.matcher(orig);
		m.find();
		System.err.println(orig.substring(m.end()));
		String remBracks = "\\{\\{[\\w\\s\\\\n\\-]*\\}\\}";
		String output = orig.substring(m.end()-1).replaceAll(remBracks, "");
		String remHead = "[=]+[\\s\\w]*[=]+";
		output = output.replaceAll(remHead, " ");
		System.out.println("NEW-" + output);
		String repNl = "(\\\\n)+";
		output = output.replaceAll(repNl, "\n");
		String charRem = "[\\[\\]\\{\\}\\\\]+";
		String remMarkup = "<.*>";
		output = output.replaceAll(remMarkup,"");
		output = output.replaceAll(charRem, " ");
		output = output.replaceAll("\\b(?![-'\\.,])(?<![-'\\.,])"," ");
		output = output.replaceAll("\\*", "");
		output = output.replaceAll("#", "");
		return output;
	}

}
