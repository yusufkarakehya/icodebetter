var xtheme='', xuserTip=false;
if (document.location.search.indexOf('xuser_tip=') >= 0) {
  xuserTip=document.location.search.split('xuser_tip=')[1].split('&')[0];
}
function changeTheme(xtheme){
  var c=document.body.className.split(' ')[0];
  if(xtheme)c+=' '+xtheme;
  document.body.className=c;
}
if (document.location.search.indexOf('layout=') >= 0) {
  xtheme=document.location.search.split('layout=')[1].split('&')[0];
}
if (document.location.search.indexOf('color=') >= 0) {
  xtheme+=' '+document.location.search.split('color=')[1].split('&')[0];
}
if(xtheme)changeTheme(xtheme);

var mid=false, msm=false;
if (document.location.search.indexOf('mid=') >= 0) {
  mid=document.location.search.split('mid=')[1].split('&')[0];
  parent.iwb.mobileChannel(mid, false, false, changeTheme);
} else {
  alert('Error. Please define Mobile iFrameID');
}