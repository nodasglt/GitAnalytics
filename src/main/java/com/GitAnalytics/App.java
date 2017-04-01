package com.GitAnalytics;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

public class App
{   
    public static void main( String[] args ) throws Exception
    {
        try (PrintWriter writer = new PrintWriter(args[1], "UTF-8")) {
            Map<Integer, Object> pies = new HashMap<>();
            
            int id = 0;
            
            System.out.println(args[0]);
            Repository repo = new FileRepository(args[0] + "/.git");
            Git git = new Git(repo);
            
            List<CommitInfo> allCommits = CommitInfo.getCommits(repo, git.log().all().call(), git.tagList().call());
            
            List<BranchInfo> branches = BranchInfo.getBranches(git, allCommits);
            Map<String, AuthorInfo> authors = AuthorInfo.getAllAuthors(git, branches, allCommits);
            
            int lineCount = 0;
            try
            {
                for (CommitInfo commit : allCommits)
                {
                    lineCount += commit.getAddedLinesNum() - commit.getDeletedLinesNum();
                }
            }
            catch (Exception e) {}
            
                        
            Ref head = repo.findRef(Constants.HEAD);
            int fileNum = 0;

            try (RevWalk walk = new RevWalk(repo)) {
                RevCommit commit = walk.parseCommit(head.getObjectId());
                RevTree tree = commit.getTree();
                try (TreeWalk treeWalk = new TreeWalk(repo)) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    while (treeWalk.next())
                    {
                       fileNum++;
                    }
                }
            }
            
            writer.println(
                    "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "  <title>GitAnalytics</title>\n" +
                            "  <meta charset=\"utf-8\">\n" +
                            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                            "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\n" +
                            "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js\"></script>\n" +
                            "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>\n" +
                            "  <style>\n" +
                            "  body {\n" +
                            "      position: relative;\n" +
                            "      background-color: #778899;\n" +
                            "  }\n" +
                            "  .pre-scrollable::-webkit-scrollbar-track\n" +
                            "  {\n" +
                            "	-webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);\n" +
                            "	border-radius: 10px;\n" +
                            "	background-color: #F5F5F5;\n" +
                            "  }\n" +
                            "  .pre-scrollable::-webkit-scrollbar\n" +
                            "  {\n" +
                            "	width: 12px;\n" +
                            "	background-color: #F5F5F5;\n" +
                            "  }\n" +
                            "  .pre-scrollable::-webkit-scrollbar-thumb\n" +
                            "  {\n" +
                            "	border-radius: 10px;\n" +
                            "	-webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);\n" +
                            "	background-color: #000080;\n" +
                            "  }\n" +
                            "  </style>\n" +
                            "</head>\n" +
                            "\n" +
                            "<body>");
            
            //5
            writer.println("<br><div class=\"row\">\n" +
                           "    <div class=\"btn-primary btn col-xs-2\">\n" +
                           "        TotalFileNum <span class=\"badge\">" + fileNum + "</span>\n" +
                           "    </div>\n" +
                           "    <div class=\"btn-primary btn col-xs-2\">\n" +
                           "        TotalCommitNum <span class=\"badge\">" + allCommits.size() + "</span>\n" +
                           "    </div>\n" +
                           "    <div class=\"btn-primary btn col-xs-2\">\n" +
                           "        TotalLines <span class=\"badge\">" + lineCount + "</span>\n" +
                           "    </div>\n" +
                           "    <div class=\"btn-primary btn col-xs-2\">\n" +
                           "        TagNum <span class=\"badge\">" + git.tagList().call().size() + "</span>\n" +
                           "    </div>\n" +
                           "    <div class=\"btn-primary btn col-xs-2\">\n" +
                           "        BranchNum <span class=\"badge\">" + branches.size() + "</span>\n" +
                           "    </div>\n" +
                           "    <div class=\"btn-primary btn col-xs-2\">\n" +
                           "        AuthorNum <span class=\"badge\">" + authors.size() + "</span>\n" +
                           "    </div>\n" +
                           "</div><br>");
            
            //4
            for (BranchInfo branch : branches)
            {
                writer.println("<button type=\"button\" class=\"row btn btn-primary\" data-toggle=\"collapse\" data-target=\"#" + (++id) + "\" style=\"width:80%\">\n" +
                        "    <div class=\"col-xs-4\">" + branch.getBranchRef().getName() + "</div>\n" +
                                "    <div class=\"col-xs-8\">[" + branch.getCreationDate() + " - " + branch.getLastCommitDate()+ "]</div>\n" +
                                        "</button>");
                writer.println("<div id=\"" + id + "\" class=\"pre-scrollable collapse\" data-offset=\"50\" style=\"height:500px; margin-right:30px; margin-left:30px; margin-top:10px; margin-bottom: 10px; border-radius: 25px; border-style: solid;\">");
                
                branch.getCommits().forEach((commit) ->
                {
                    writer.println("<div class=\"panel panel-default\">\n" +
                            "   <div class=\"panel-heading\"> commit: " + commit.getId() + "</div>\n" +
                                    "   <div class=\"panel-body\">\n" +
                                    "       <p> Author: " + commit.getAuthorName() + "</p>\n" +
                                            "       <p> Date: " + commit.getCreationDate() + "</p>\n" +
                                                    "       <p></p>\n" +
                                                    "       <p> Message: " + commit.getMessage() + "</p>\n" +
                                                            "   </div>\n" +
                                                            "</div>");
                });
                writer.println("</div>");
            }
            
            writer.println("<script src=\"https://code.highcharts.com/highcharts.js\"></script>\n" + 
                    "<script src=\"https://code.highcharts.com/modules/exporting.js\"></script>\n" +
                    "<br><br>\n" +
                    "<div class=\"row\">\n" +
                    "    <div id=\"3\" class=\"col-xs-4\" style=\"min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto\"></div>\n" +
                    "    <div id=\"4\" class=\"col-xs-4\" style=\"min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto\"></div>\n" +
                    "    <div id=\"5\" class=\"col-xs-4\" style=\"min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto\"></div>\n" +
                    "</div>\n" +
                    "<br>\n" +
                    "<div id=\"6\" style=\"min-width: 1800px; height: 1000px; max-width: 600px; margin: 0 auto\"></div>\n" +
                    "<br>\n" +
                    "<div id=\"7\" style=\"min-width: 1800px; height: 1000px; max-width: 600px; margin: 0 auto\"></div>");
            
            //5
            PieDiagram commitsPerAuthor = new PieDiagram("Commits Per Author", "{series.name}: <b>{point.percentage:.1f}%</b>");
            authors.entrySet().forEach((author) ->
            {
                commitsPerAuthor.add(author.getKey(), author.getValue().getTotalCommits());
            });
            pies.put(3, commitsPerAuthor);
            
            BarDiagram commitsPerBranch = new BarDiagram("Commits Per Branch (with overlaps)", false).addBarSet("commits");
            branches.forEach((branch) ->
            {
                commitsPerBranch.addCategory(branch.getBranchRef().getName());
                commitsPerBranch.add("commits", branch.getCommits().size());
            });
            pies.put(4, commitsPerBranch);
            
            BarDiagram commitsPerAuthorPerBranch = new BarDiagram("Commits Per Author Per Branch (with overlaps)", true);
            branches.forEach((BranchInfo branch) ->
            {
                commitsPerAuthorPerBranch.addCategory(branch.getBranchRef().getName());
            });
            authors.entrySet().forEach((Map.Entry<String, AuthorInfo> author) ->
            {
                commitsPerAuthorPerBranch.addBarSet(author.getKey());
                
                branches.forEach((BranchInfo branch) ->
                {
                    commitsPerAuthorPerBranch.add(author.getKey(), author.getValue().getTotalCommitsOnBranch(branch));
                });
            });
            pies.put(5, commitsPerAuthorPerBranch);
            
            BarDiagram commitDensity = new BarDiagram("Commit Density Per Author", true);
            commitDensity.addBarSet("Total Commits").addBarSet("Time Span (days)");
            authors.entrySet().forEach((Map.Entry<String, AuthorInfo> author) ->
            {
                commitDensity.addCategory(author.getKey());
                
                LinkedList<CommitInfo> commits = author.getValue().getCommits();
                long diff = Math.abs(commits.getLast().getCreationDate().getTime() - commits.getFirst().getCreationDate().getTime());
                long diffDays = (long)Math.ceil(diff / (24.0f * 60 * 60 * 1000)) + 1;
                
                commitDensity.add("Total Commits", author.getValue().getCommits().size());
                commitDensity.add("Time Span (days)", (int)diffDays);
            });
            pies.put(6, commitDensity);
            
            BarDiagram commitDiff = new BarDiagram("Commit Diff Per Author", true);
            commitDiff.addBarSet("Lines Added").addBarSet("Lines Changed").addBarSet("Lines Deleted");
            authors.entrySet().forEach((Map.Entry<String, AuthorInfo> author) ->
            {
                commitDiff.addCategory(author.getKey());
                
                int add = 0, ch = 0, del = 0;
                for(CommitInfo commit : author.getValue().getCommits())
                {
                    add += commit.getAddedLinesNum();
                    ch  += commit.getChangedLinesNum();
                    del += commit.getDeletedLinesNum();
                }
                
                commitDiff.add("Lines Added", add);
                commitDiff.add("Lines Changed", ch);
                commitDiff.add("Lines Deleted", del);
            });
            pies.put(7, commitDiff);
            
            String script = "<script>\n" +
                    "    $(document).ready(function()\n" +
                    "    {\n";
            
            script = pies.entrySet().stream().map((e) -> "        Highcharts.chart('" + e.getKey() + "', " + e.getValue() + ");\n").reduce(script, String::concat);
            
            writer.println(script + "    });\n" +
                    "</script>");
            
            writer.println("</body></html>");
        }
    }
}
