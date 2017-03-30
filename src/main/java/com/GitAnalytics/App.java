package com.GitAnalytics;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class App
{   
    public static void main( String[] args ) throws Exception
    {
        PrintWriter writer = new PrintWriter(args[1], "UTF-8");
    
        System.out.println(args[0]);
        Repository repo = new FileRepository(args[0] + "/.git");
        Git git = new Git(repo);
        
        List<BranchInfo> branches = BranchInfo.getBranches(repo, git);
        System.out.println("Branches done!");
        Map<String, AuthorInfo> authors = AuthorInfo.getAllAuthors(repo, git);
        
       //2
       branches.forEach((branch) ->
       {
           int commitCount = 0;
           int lineCount = 0;
           try
           {
                for (RevCommit commit : git.log().add(branch.getBranchRef().getObjectId()).call())
                {
                    CommitInfo commitInfo = new CommitInfo(repo, commit, null);
                    lineCount += commitInfo.getAddedLinesNum() - commitInfo.getDeletedLinesNum();
                    commitCount++;
                    System.out.print(commitCount + "\r");
                }
           }
           catch (Exception e) {}
           
           System.out.println(branch.getBranchRef().getName() + " lines: " + lineCount);
       });      

       //3
       System.out.println("TagNum: " + git.tagList().call().size());
       System.out.println("BranchNum: " + branches.size());
       System.out.println("AuthorNum: " + authors.size());
       
       //4
       System.out.println(branches);
       branches.forEach((branch) -> {
           System.out.println(branch.getCommits());
        });
       
       //5
       int totalCommitNum = 0;
       for (RevCommit commit : git.log().all().call())
       {
           totalCommitNum++;
       }
       System.out.println("TotalCommitNum: " + totalCommitNum);
       for (Map.Entry<String, AuthorInfo> author : authors.entrySet())
       {
           double per = (double)author.getValue().getTotalCommits() / totalCommitNum * 100.0f;
           System.out.println(author.getKey() + " " + per + "%");
       }
       for (BranchInfo branch : branches)
       {
           double per = (double)branch.getCommits().size() / totalCommitNum * 100.0f;
           System.out.println(branch.getBranchRef().getName() + " " + per + "%");
       }
       branches.forEach((branch) -> {
           int total = branch.getCommits().size();
           
           authors.entrySet().forEach((author) ->
           {
               double per = 100.0f * ((double)author.getValue().getTotalCommits() / total);
               System.out.println(branch.getBranchRef().getName() + " :: " + author.getKey() + " " + per + "%");
           });
        });
       
       //6
       authors.entrySet().forEach((author) ->
       {
           LinkedList<CommitInfo> commits = author.getValue().getCommits();
           long diff = Math.abs(commits.getLast().getCreationDate().getTime() - commits.getFirst().getCreationDate().getTime());
           long diffDays = diff / (24 * 60 * 60 * 1000);

           System.out.println(author.getKey() + " :: " + author.getValue().getCommits().size() + " // " + diffDays);
       });
       
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
       
       writer.close();
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
