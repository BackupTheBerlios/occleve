/* Copyright (c) 2000 Michael Claßen <mclassen@internet.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id: Node.java,v 1.3 2008/03/02 10:12:57 joe_gittings Exp $
 */

package com.exploringxml.xml;

import java.util.Hashtable;
import java.util.Vector;

/**
 * A node of an XML DOM tree;
 *
 * @author    Michael Claßen
 * @version   $Revision: 1.3 $
 * 
 * Trivially modified by Joe Gittings 29Dec2006.
 * More substantial modifications by Joe Gittings March 2008:
 * added the findAllChildElements() method.
 */
public class Node {

  public String type;
  public String name;
  public String value;
  public Hashtable attributes;
  public int uid;

  public JSArray contents;

  // Joe Gittings 30Dec2006
  public Vector getContents() {return contents.toVector();}

  public JSArray index;

  // node types
  static final String Element = "element";
  static final String CharData = "chardata";
  static final String PI = "pi";
  static final String Comment = "comment";

/////////////////////////
//// the object constructors for the hybrid DOM

  /**
   * factory method for element nodes
   *
   * @return    a Node of type element
   */
  static Node createElement() {
    Node n = new Node();
  	n.type = Element;
	  n.name = new String();
  	n.attributes = new Hashtable();
	  n.contents = new JSArray();
  	n.uid = Xparse.count++;
	  Xparse.index.setElementAt(n, n.uid);
    return n;
  }

  /**
   * factory method for the root element
   *
   * @return    a rootelement Node
   */
  static Node createRootelement() {
    return createElement();
  }

  /**
   * factory method for chardata nodes
   *
   * @return    a chardata Node
   */
  static Node createChardata() {
    Node n = new Node();
  	n.type = CharData;
	  n.value = new String();
    return n;
  }

  /**
   * factory method for PI nodes
   *
   * @return    a PI Node
   */
  static Node createPi() {
    Node n = new Node();
	  n.type = PI;
  	n.value = new String();
    return n;
  }

  /**
   * factory method for comment nodes
   *
   * @return    a comment Node
   */
  static Node createComment()
  {
    Node n = new Node();
	  n.type = Comment;
  	n.value = new String();
    return n;
  }

  /**
   * returns the character data in the first child element;
   * returns nonsense if the first child element ist not chardata
   *
   * @return    the characters following an element
   */
  public String getCharacters() {
    return ((Node)contents.elementAt(0)).value;
  }

  /**
   * find the node matching a certain occurrence of the path description
   *
   * @param     path an XPath style expression without leading slash
   * @param     occur array indicating the n'th occurrence of a node matching each simple path expression
   *
   * @return    the n'th Node matching the path description
   */
  public Node find(String path, int[] occur) {
    Node n = this;
    JSArray a = new JSArray();
    a.split(path, "/");
    int i = 0;
    while (i < a.length()) {

trace("--------------------------");
trace("n = " + n);
trace("n.name = " + n.name);
trace("n.contents = " + n.contents);

      n = findChildElement(n, (String)a.elementAt(i), occur[i]);
      if (n == null) return null;
      i++;
    }
    return n;
  }

  /**
   * find the child node matching a certain occurrence of a simple path description
   *
   * @param     parent the parent node to start from
   * @param     simplePath one element of an XPath style expression
   * @param     occur the n'th occurance of a node matching the path expression
   *
   * @return    the n'th child Node matching the simple path description
   */
  Node findChildElement(Node parent, String simplePath, int occur)
  {
	trace("#####################################################################");  
    trace("Entering findChildElement");
    trace("Looking for " + occur + "th occurrence of " + simplePath);

    JSArray a = parent.contents;
    trace("Got JSArrary a OK: it = " + a);
    
    Node n;
    int  found = 0;
    int i = 0;
    String tag;
    do {
      n = (Node)a.elementAt(i);
      ++i;
      tag = (n.name != null) ? n.name : "";
      int colonPos = tag.indexOf(':');
      tag = (colonPos == -1) ? tag : tag.substring(colonPos + 1);

      trace("=======================");
      trace("occurrences found = " + found);
      trace("simplePath = " + simplePath);
      trace("tag = " + tag);

      // Modified by Joe Gittings 29Dec2006: do the comparison in lower case
      if (simplePath.toLowerCase().equals(tag.toLowerCase())) ++found;

    } while (i < a.length() && found < occur);
    return (found == occur) ? n : null;
  }

  /**Joe Gittings 2nd March 2008.
  Returns all nodes matching simplePath which are children of parentNode.
  Repeatedly using findChildElement instead is very inefficient because the code runs
  through all preceding matching nodes each time.*/
  public Node[] findAllChildElements(Node parentNode, String simplePath)
  {
    	trace("Entering findAllChildElements()");
	    Vector vResults = new Vector();
	  
	    JSArray a = parentNode.contents;
	    trace("Got JSArrary a OK: it = " + a);
	    
	    Node n;
	    int  found = 0;
	    int i = 0;
	    String tag;
	    do {
	      n = (Node)a.elementAt(i);
	      ++i;
	      tag = (n.name != null) ? n.name : "";
	      int colonPos = tag.indexOf(':');
	      tag = (colonPos == -1) ? tag : tag.substring(colonPos + 1);

	      // Modified by Joe Gittings 29Dec2006: do the comparison in lower case
	      if (simplePath.toLowerCase().equals(tag.toLowerCase()))
    	  {
	    	  ++found;
	    	  vResults.addElement(n);

	    	  trace("=======================");
		      trace("occurrences found = " + found);
		      trace("simplePath = " + simplePath);
		      trace("tag = " + tag);
    	  }

	    } while (i < a.length());
	    
	    Node[] nodes = new Node[vResults.size()];
	    vResults.copyInto(nodes);
	    return nodes;
  }
  
  // Joe Gittings 30Dec2006
  protected void trace(String s)
  {
      ////System.out.println(s);
  }

  // Joe Gittings 30Dec2006
  public Node findFirst(String path)
  {
      int[] first = {1};
      return find(path,first);
  }

}

