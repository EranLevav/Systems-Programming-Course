package bgu.spl181.net.impl.protocols;

import bgu.spl181.net.api.MessageEncoderDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineMessageEncoderDecoder implements MessageEncoderDecoder<List<String>> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    @Override
    public List<String> decodeNextByte(byte nextByte) {
        if (nextByte == '\n') {
            return popString();
        }
        
        pushByte(nextByte);
        return null; //not a line yet
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private List<String> popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        result = result.trim(); // ensure no /n in the end
        return stringToList(result);
    }
    
    private List<String> stringToList(String result){
	    List<String> messageAsList = new ArrayList<String>();
		String pattern = "((country=)|\"([^\"]*)\"|[^ ]*)";
	    Matcher m =  Pattern.compile(pattern).matcher(result);
	    while(m.find()) {
        	{
        		String subString=m.group();
        		if (!subString.isEmpty() && !subString.equals("country=")){
        			if(messageAsList.size()==2 && (messageAsList.get(0).equals("REGISTER")|messageAsList.get(0).equals("LOGIN"))){
        				messageAsList.add(subString);
        			}
        			else{
        				messageAsList.add((subString.replaceAll("\"","")));
        			}
        		}
        	}
	    }	
	    return messageAsList;
    }

	@Override
	public byte[] encode(List<String> message) {
		String output="";
		for(String s: message)
			output+= s+" ";
		output= output.trim()+'\n';
		return output.getBytes(); //uses utf8 by default
	}
}
