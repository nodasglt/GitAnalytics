/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GitAnalytics;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
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

    private BranchInfo(Ref ref, LinkedList<CommitInfo> commits) throws Exception
    {
        mBranchRef = ref;
        
        mCommits = commits;

        mCreationDate = mCommits.getLast().getCreationDate();
        mLastCommitDate = mCommits.getFirst().getCreationDate();
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

    public static List<BranchInfo> getBranches(Git git, List<CommitInfo> allCommits) throws Exception
    {
        List<BranchInfo> list = new LinkedList<>();
        
        List<Ref> tags = git.tagList().call();

        for (Ref branch : git.branchList().call())
        {
            LinkedList commits = new LinkedList<>();
            
            Set<RevCommit> branchCommits = new HashSet<>();
            for (RevCommit commit : git.log().add(branch.getObjectId()).call())
            {
                branchCommits.add(commit);
            }
            
            allCommits.stream().filter((commit) -> (branchCommits.contains(commit.getCommit()))).forEachOrdered((commit) ->
            {
                commits.add(commit);
            });
                
            list.add(new BranchInfo(branch, commits));
        }

        return list;
    }
};
