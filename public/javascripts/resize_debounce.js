// MIT license - https://github.com/louisremi/jquery-smartresize

on_resize(function() {
  // handle the resize event here
  drawMusic();
  console.log("width is " + $("#musicdiv").width());
});

// debulked onresize handler
function on_resize(c,t){onresize=function(){clearTimeout(t);t=setTimeout(c,100)};return c};