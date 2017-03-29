/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GitAnalytics;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 *
 * @author nodas
 */
public class BranchInfo
{
    private final Ref mBranchRef;
    private final Date mCreationDate;
    private final Date mLastCommitDate;    
    private final LinkedList<CommitInfo> mCommits;

    private BranchInfo(Repository repo, Ref ref, Iterable<RevCommit> commits, List<Ref> tags) throws Exception
    {
        mBranchRef = ref;
        
        mCommits = new LinkedList<>();

        for (RevCommit commit : commits)
        {
           //4
            LinkedList<String> curTags = new LinkedList<>();
            for (Ref tag : tags)
            {
                if (tag.getObjectId().equals(ObjectId.fromString(commit.getName())))
                {
                    curTags.add(tag.getName());
                }
            }

            mCommits.add(new CommitInfo(repo, commit, curTags));
        }

        mCreationDate = mCommits.getFirst().getCreationDate();
        mLastCommitDate = mCommits.getLast().getCreationDate();
    }

    public Ref getBranchRef()
    {
        return mBranchRef;
    }

    public Date getCreationDate()
    {
        return mCreationDate;
    }

    public Date getLastCommitDate()
    {
        return mLastCommitDate;
    }

    public List<CommitInfo> getCommits()
    {
        return mCommits;
    }
    
    @Override
    public String toString()
    {
        return mBranchRef.getName() + " Created: " + mCreationDate + " Last: " + mLastCommitDate;
    }

    public static List<BranchInfo> getBranches(Repository repo, Git git) throws Exception
    {
        List<BranchInfo> list = new LinkedList<>();
        
        List<Ref> tags = git.tagList().call();

        for (Ref branch : git.branchList().call())
        {
            list.add(new BranchInfo(repo, branch, git.log().add(branch.getObjectId()).call(), tags));
        }

        return list;
    }
};
