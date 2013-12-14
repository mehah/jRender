var MAIN_ELEMENT_ID = 1;
var WINDOW_ID = 2;
var DOCUMENT_ID = 3;
var HEAD_ID = 4;
var BODY_ID = 5;

function removeRegisteredReturn(uid)
{
	delete Greencode.tag.references[uid+""];
}

var Greencode = {
	ready: null,
	
	comets: new Array(),
	tag:{
		references: {},
		getById: function(id, mainElement)
		{	
			var tag;
			if(id === MAIN_ELEMENT_ID)
				tag = mainElement;
			else if(id === WINDOW_ID)
				tag = window;
			else if(id === DOCUMENT_ID)
				tag = document;
			else if(id === HEAD_ID)
				tag = document.head;
			else if(id === BODY_ID)
				tag = document.body;
			else if((tag = Greencode.tag.references[id+""]) != null)
			{
				/*
				 * Remover a tag da memória, caso não esteja vinculada no html.
				 */
				/*if(tag.parents('body').length === 0)
				{
					delete Greencode.tag.references[id+""];
					return null;
				}*/
			}
			
			return tag;
		}/*,
		set: function(ref, tag)
		{
			Greencode.tag.references[ref+""] = tag;
		}*/
	}
};