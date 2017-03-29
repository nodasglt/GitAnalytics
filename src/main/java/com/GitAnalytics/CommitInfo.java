/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GitAnalytics;

import java.util.Date;
import java.util.List;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.Edit.Type;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
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

    public CommitInfo(Repository repo, RevCommit commit, List<String> tags)
    {
        mCommit = commit;
        mTags = tags;
        mAuthor = mCommit.getAuthorIdent();
        mLinesChanged = new int[3];
        
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
                    mLinesChanged[0] = edit.getEndA() - edit.getBeginA();
                    mLinesChanged[2] = edit.getEndB() - edit.getBeginB();

                    if (edit.getType() == Type.REPLACE)
                    {
                        mLinesChanged[1] = mLinesChanged[2] - mLinesChanged[0];
                    }
                }
            }
        }
        catch (Exception e) {}
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
}
