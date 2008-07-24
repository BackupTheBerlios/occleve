package org.occleve.mobileclient.dictionary;

public class FileOffsets
{
	private int[] m_FileOffsets;
	
	public FileOffsets(int iFileOffset)
	{
    	m_FileOffsets = new int[1];
    	m_FileOffsets[0] = iFileOffset;
	}
	
	/**Since each line is indexed in sequence, we don't have to worry about duplicates.*/
	public void addNewOffset(int iFileOffset)
	{
		int[] newArray = new int[m_FileOffsets.length];
		System.arraycopy(m_FileOffsets,0,newArray,0,m_FileOffsets.length);
		m_FileOffsets = newArray;
	}
}
