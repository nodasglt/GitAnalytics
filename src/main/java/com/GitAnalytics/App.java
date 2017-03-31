package com.GitAnalytics;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.json.JSONArray;
import org.json.JSONObject;

public class App
{   
    public static void main( String[] args ) throws Exception
    {
        PrintWriter writer = new PrintWriter(args[1], "UTF-8");
        
        int id = 0;
        
        System.out.println(args[0]);
        Repository repo = new FileRepository(args[0] + "/.git");
        Git git = new Git(repo);
        
        List<CommitInfo> allCommits = CommitInfo.getCommits(repo, git.log().all().call(), git.tagList().call());
        
        writer.println(
        "<!DOCTYPE html>\n" +
        "<html>\n" +
        "<head>\n" +
        "  <title>Bootstrap Example</title>\n" +
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
        writer.println("<div>TotalCommitNum: " + allCommits.size() + "</div>");
        
        
        List<BranchInfo> branches = BranchInfo.getBranches(git, allCommits);
        Map<String, AuthorInfo> authors = AuthorInfo.getAllAuthors(git, branches, allCommits);
        
        branches.add(branches.get(0));
       
        //2
        branches.forEach((branch) ->
        {
            int commitCount = 0;
            int lineCount = 0;
            try
            {
                for (CommitInfo commit : branch.getCommits())
                {
                    lineCount += commit.getAddedLinesNum() - commit.getDeletedLinesNum();
                    commitCount++;
                }
            }
            catch (Exception e) {}
           
            writer.println(branch.getBranchRef().getName() + " lines: " + lineCount);
        });      

        //3
        writer.println("<div>TagNum: " + git.tagList().call().size() + "</div>");
        writer.println("<div>BranchNum: " + branches.size() + "</div>");
        writer.println("<div>AuthorNum: " + authors.size() + "</div>");
       
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
                       "    <div id=\"pie1\" class=\"col-xs-4\" style=\"min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto\"></div>\n" +
                       "    <div id=\"pie2\" class=\"col-xs-4\" style=\"min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto\"></div>\n" +
                       "    <div id=\"pie3\" class=\"col-xs-4\" style=\"min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto\"></div>\n" +
                       "</div>");
       
        //5
        writer.println("<ul>");
        authors.entrySet().forEach((author) ->
        {
            double per = (double)author.getValue().getTotalCommits() / allCommits.size() * 100.0f;
            writer.println("<li>" + author.getKey() + " " + per + "%</li>");
        });
        writer.println("</ul>");
        writer.println("<ul>");
        branches.forEach((branch) ->
        {
           double per = (double)branch.getCommits().size() / allCommits.size() * 100.0f;
           writer.println("<li>" + branch.getBranchRef().getName() + " " + per + "%</li>");
        });
        writer.println("</ul>");
        writer.println("<ul>");
        branches.forEach((BranchInfo branch) ->
        {
           int total = branch.getCommits().size();
           
           authors.entrySet().forEach((Map.Entry<String, AuthorInfo> author) ->
           {
               double per = 100.0f * ((double)author.getValue().getTotalCommits() / total);
               writer.println("<li>" + branch.getBranchRef().getName() + " :: " + author.getKey() + " " + per + "%</li>");
           });
        });
        writer.println("</ul>");
        writer.println("<ul>");
        //6
        authors.entrySet().forEach((Map.Entry<String, AuthorInfo> author) ->
        {
            LinkedList<CommitInfo> commits = author.getValue().getCommits();
            long diff = Math.abs(commits.getLast().getCreationDate().getTime() - commits.getFirst().getCreationDate().getTime());
            long diffDays = (long)Math.ceil(diff / (24.0f * 60 * 60 * 1000)) + 1;

            int totalCommits = author.getValue().getCommits().size();
            int perDay = totalCommits / (int)diffDays;
            int perMonth = 30 * perDay;
            if (perMonth > totalCommits)
            {
                perMonth = totalCommits;
            }
            int perYear = 365 * perDay;
            if (perYear > totalCommits)
            {
                perYear = totalCommits;
            }

            writer.println("<li>" + author.getKey() + " :: " + perDay + "/day " + perMonth + "/month " + perYear + "/year</li>");
        });
        writer.println("</ul>");
       
        //7
        authors.entrySet().forEach((Map.Entry<String, AuthorInfo> author) ->
        {
           int add = 0, ch = 0, del = 0;
           for(CommitInfo commit : author.getValue().getCommits())
           {
               add += commit.getAddedLinesNum();
               ch  += commit.getChangedLinesNum();
               del += commit.getDeletedLinesNum();
           }
           System.out.println(author.getKey() + " :-: a " + add + " c " + ch + " d " + del);
        });
        
        writer.println("</body></html>");
       
        writer.close();
        
        JSONObject json = new JSONObject();
        json.put("name", "student");

        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("information", "test");
        item.put("id", 3);
        item.put("name", "course1");
        array.put(item);

        json.put("course", item);

        System.out.println(json);
    }
}

/*
 Iterable<RevCommit> allCommits = git.log().all().call();
        
        for (RevCommit commit : allCommits)
        {
            RevWalk rw = new RevWalk(repo);
            try
            {
                RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                df.setRepository(repo);
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);
                //int filesChanged = diffs.size();
                for (DiffEntry diff : df.scan(parent.getTree(), commit.getTree()))
                {
                    for (Edit edit : df.toFileHeader(diff).toEditList())
                    {
                        int linesDeleted = edit.getEndA() - edit.getBeginA();
                        int linesAdded = edit.getEndB() - edit.getBeginB();

                        System.out.println(commit.getShortMessage() + " | " + linesDeleted + " " + linesAdded + " " + edit.getType());
                    }
                }
            }
            catch (Exception e) {}
        }   
*/


/*
                boolean foundInThisBranch = false;

                RevCommit targetCommit = walk.parseCommit(repo.resolve(commit.getName()));
                for (Map.Entry<String, Ref> e : repo.getAllRefs().entrySet())
                {
                    if (e.getKey().startsWith(Constants.R_HEADS))
                    {
                        if (walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId())))
                        {
                            String foundInBranch = e.getValue().getName();
                            if (branchName.equals(foundInBranch))
                            {
                                foundInThisBranch = true;
                                break;
                            }
                        }
                    }
                }
*/
