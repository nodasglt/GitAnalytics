/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GitAnalytics;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.Edit.Type;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

/**
 *
 * @author nodas
 */
public class CommitInfo
{
    private final RevCommit mCommit;
    private final List<String> mTags;
    private final PersonIdent mAuthor;
    private final int[] mLinesChanged; //Del, Ch, Add
    
    private static int I = 0;

    private CommitInfo(DiffFormatter df, RevWalk rw, RevCommit commit, List<String> tags) throws Exception
    {
        System.out.print(I + "\r"); I++;
        mCommit = commit;
        mTags = tags;
        mAuthor = mCommit.getAuthorIdent();
        mLinesChanged = new int[3];

        RevTree parent = null;
        try
        {
            parent = rw.parseCommit(commit.getParent(0).getId()).getTree();
        }
        catch (Exception e) {}
        //int filesChanged = diffs.size();
        mLinesChanged[0] = mLinesChanged[1] = mLinesChanged[2] = 0;
        for (DiffEntry diff : df.scan(parent, commit.getTree()))
        {
            for (Edit edit : df.toFileHeader(diff).toEditList())
            {
                int del = edit.getEndA() - edit.getBeginA();
                int add = edit.getEndB() - edit.getBeginB();

                if (edit.getType() == Type.REPLACE)
                {
                    mLinesChanged[1] += Math.abs(add - del);
                }
                
                mLinesChanged[2] += add;
                mLinesChanged[0] += del;
            }
        }
    }
    
    public int getDeletedLinesNum()
    {
        return mLinesChanged[0];
    }
    
    public int getChangedLinesNum()
    {
        return mLinesChanged[1];
    }
        
    public int getAddedLinesNum()
    {
        return mLinesChanged[2];
    }

    public List<String> getTags()
    {
        return mTags;
    }

    public RevCommit getCommit()
    {
        return mCommit;
    }

    public String getId()
    {
        return mCommit.getName();
    }

    public String getMessage()
    {
        return mCommit.getShortMessage();
    }

    public Date getCreationDate()
    {
        return mAuthor.getWhen();
    }

    public String getAuthorName()
    {
        return mAuthor.getName();
    }
    
    @Override
    public String toString()
    {
        return getId() + " " + getMessage() + " " + getMessage() + " " + getCreationDate() + " " + getAuthorName() + " " + getTags();
    }
    
    public static List<CommitInfo> getCommits(Repository repo, Iterable<RevCommit> commits, List<Ref> tags) throws Exception
    {
        List<CommitInfo> list = new LinkedList<>();
        
        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(repo);
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);
        
        RevWalk rw = new RevWalk(repo);
        
        int i = 0;
        
        for (RevCommit commit : commits)
        {
            List<String> curTags = new LinkedList<>();
            
            tags.stream().filter((tag) -> (tag.getObjectId().equals(ObjectId.fromString(commit.getName())))).forEachOrdered((tag) ->
            {
                curTags.add(tag.getName());
            });
                        
            list.add(new CommitInfo(df, rw, commit, curTags));
            
            if (++i > 1000) break;
        }
        
        return list;
    }
}
