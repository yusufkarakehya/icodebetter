CKEDITOR.ENTER_P	= 1;
CKEDITOR.ENTER_BR	= 2;
CKEDITOR.ENTER_DIV	= 3;

 
CKEDITOR.editorConfig = function( config )
{
    //config.filebrowserBrowseUrl = '/admin/content/filemanager.aspx?path=Userfiles/File&editor=FCK';
    config.filebrowserImageBrowseUrl = 'imageFileBrowser?pageno=1&groupbynum=6';
    config.filebrowserWindowWidth = '800';
    config.filebrowserWindowHeight = '600';
	config.autoUpdateElement = true;
	config.baseHref = '';
	config.contentsCss = CKEDITOR.basePath + 'contents.css';
	config.contentsLangDirection ='ltr';
	config.language ='tr';
	config.defaultLanguage = 'tr',
	config.enterMode = CKEDITOR.ENTER_BR;;
	config.shiftEnterMode = CKEDITOR.ENTER_BR;
	config.corePlugins ='';
	config.docType ='<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
	config.height =200;
	config.plugins ='about,basicstyles,blockquote,button,clipboard,colorbutton,contextmenu,elementspath,enterkey,entities,filebrowser,find,flash,font,format,forms,horizontalrule,htmldataprocessor,image,indent,justify,keystrokes,link,list,maximize,newpage,pagebreak,pastefromword,pastetext,popup,preview,print,removeformat,resize,save,scayt,smiley,showblocks,sourcearea,stylescombo,table,tabletools,specialchar,tab,templates,toolbar,undo,wysiwygarea,wsc';
	config.theme ='default';
	config.skin ='office2003';
	config.toolbar_Full = [['Preview', '-', 'Print'],	                       
                           ['Undo', 'Redo'], ['Cut', 'Copy', 'Paste', 'PasteFromWord', 'SelectAll'], 
                           ['Find', 'Replace'],
                           ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ],
                           [ 'Image','Table','HorizontalRule','SpecialChar','PageBreak','Iframe' ],
                           '/',
                           [ 'Styles','Format','Font','FontSize','NumberedList','BulletedList'],
                           ['Bold', 'Italic', 'Underline', 'Strike', '-', 'Subscript', 'Superscript','TextColor','BGColor'],
                           ['Source']];
	config.toolbar="Full";
};


// PACKAGER_RENAME( CKEDITOR.config )
/*
config.toolbar_Full =
	[
		{ name: 'document', items : [ 'Source','-','Save','NewPage','DocProps','Preview','Print','-','Templates' ] },
		{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
		{ name: 'editing', items : [ 'Find','Replace','-','SelectAll','-','SpellChecker', 'Scayt' ] },
		{ name: 'forms', items : [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 
	        'HiddenField' ] },
		'/',
		{ name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
		{ name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','CreateDiv',
		'-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ] },
		{ name: 'links', items : [ 'Link','Unlink','Anchor' ] },
		{ name: 'insert', items : [ 'Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak','Iframe' ] },
		'/',
		{ name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
		{ name: 'colors', items : [ 'TextColor','BGColor' ] },
		{ name: 'tools', items : [ 'Maximize', 'ShowBlocks','-','About' ] }
	];
	 
	config.toolbar_Basic =
	[
		['Bold', 'Italic', '-', 'NumberedList', 'BulletedList', '-', 'Link', 'Unlink','-','About']
	];*/