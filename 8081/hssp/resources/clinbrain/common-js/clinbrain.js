//删除左右两端的空格
function trim(str)
{
	return str.replace(/(^\s*)|(\s*$)/g, "");
}
String.prototype.trim = function()
{
	return this.replace(/(^\s*)|(\s*$)/g, "");
}
//删除左边的空格
function ltrim(str)
{
	return str.replace(/(^\s*)/g,"");
}
String.prototype.ltrim = function()
{
	return this.replace(/(^\s*)/g,"");
}
//删除右边的空格
function rtrim(str)
{
	return str.replace(/(\s*$)/g,"");
}
String.prototype.rtrim = function()
{
	return this.replace(/(\s*$)/g,"");
}