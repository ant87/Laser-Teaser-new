package com.anthonygascoigne.photonfinish;




/**
 *
 * @author Owner
 */
public class Moveable
{ 
    
    protected int boundingWidth = 0;
    protected int boundingHeight = 0;
    
    
	public void setBoundingRect(int width, int height)
	{
		this.boundingWidth = width;
		this.boundingHeight = height;

	}    
}
