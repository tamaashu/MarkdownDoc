## Introduction

MarkdownDoc is a tool that basically does what the name sounds like. My intention with this tool was to be able to
document my java opensource tools in markdown and be able to generate both html and PDF from it using a maven plugin.

So why not use mavens site plugin which does support markdown ? Since maven3 the site plugin is not what it used to 
be and these days generating a whole site for your project seems a bit much. Both Bitbucket and GitHub supports 
markdown documentation right off in a nice and easy way. I want to choose where to put my documentation (ok, most
locations in maven can be configured) and I also had the following requirements:

* Be able to generate one PDF document from a whole collection of separate markdown documents so that I can spread
  them out in different subproject for multi maven project projects. If you are reading this in PDF format this
  PDF have been put together from multiple sources. 

* Be able to generate a table of contents and a title page. 

* I just wanted to do it my way OK! :-)

It does also provide a java -jar executable variant. The main functionality is available as a library.

It is unfortunately written in Groovy, something I start to regret and consider a mistake. I might redo it in plain
java later, but at the moment it is what it is.


### How markdown is MarkdownDoc ?

Well, it implements the "specification" as documented on [daringfireball.net](http://daringfireball.net/projects/markdown/syntax).
This specification however is not extremely exact so there might be some differences.

The known (and intentional) differences are:

* No HMTL pass-through! Well, there is a small exception to that. HTML comments are passed along. Mostly because
  there is no markdown comment format and I wanted to be able to put comments in my documents. The reason for
  no HTML pass-through is that MarkdownDoc takes it directly from markdown to PDF without any HTML rendering
  in between. The main purpose of this tool is to write documentation not generate HTML sites.

* Escaping with '\\'. In MarkdownDoc you can escape any character with \\ and it will be passed through as is
  without being acted on if it has markdown meaning.

* No entity encoding of email addresses.

* No multiple block quote levels (as of now).